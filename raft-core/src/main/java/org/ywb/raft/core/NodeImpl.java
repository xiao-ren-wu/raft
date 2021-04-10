package org.ywb.raft.core;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.schedule.task.LogReplicationTask;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.NodeStore;
import org.ywb.raft.core.support.meta.GroupMember;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.support.msg.*;
import org.ywb.raft.core.support.role.AbstractNodeRole;
import org.ywb.raft.core.support.role.CandidateNodeRole;
import org.ywb.raft.core.support.role.FollowerNodeRole;
import org.ywb.raft.core.support.role.LeaderNodeRole;
import org.ywb.raft.core.utils.Assert;

import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:23 上午 星期四
 * @since 1.0.0
 */
@Slf4j
public class NodeImpl implements Node{

    /**
     * 组件核心上下文
     */
    private final NodeContext context;

    /**
     * 组件是否已经启动
     */
    private boolean started;

    /**
     * 当前角色以及信息
     */
    private AbstractNodeRole role;

    public NodeImpl(NodeContext context) {
        this.context = context;
    }

    @Override
    public void start() {
        if (started) {
            return;
        }
        // 注册到自己的eventBus
        context.getEventBus().register(this);
        // 初始化连接器
        context.getConnector().initialize();
        // 启动时为follower
        NodeStore store = context.getNodeStore();
        changeToRole(new FollowerNodeRole(store.getTerm(), store.getVotedFor(), null, scheduleTimeout()));
        started = true;
    }

    @Override
    public void stop() throws InterruptedException {
        Assert.isFalse(started, () -> new IllegalStateException("node not started."));
        // 关闭定时器
        context.getScheduler().stop();
        // 关闭连接器
        context.getConnector().close();
        // 关闭执行器
        context.getTaskExecutor().shutdown();

        started = false;
    }

    private void changeToRole(AbstractNodeRole nodeRole) {
        log.debug("node {},role state changed -> {}", context.getSelfId(), nodeRole);
        NodeStore store = context.getNodeStore();
        store.setTerm(nodeRole.getTerm());
        if (nodeRole.getName() == RoleName.FOLLOWER) {
            store.setVotedFor(((FollowerNodeRole) nodeRole).getVotedFor());
        }
        this.role = nodeRole;
    }

    private ElectionTimeoutTask scheduleTimeout() {
        return context.getScheduler().scheduleElectionTimeoutTask(this::electionTimeout);
    }

    /**
     * 1. 选举超时需要变更节点角色
     * 2. 发送RequestVote消息给其他节点
     */
    private void electionTimeout() {
        context.getTaskExecutor().submit(this::doProcessElectionTimeout);
    }

    private void doProcessElectionTimeout() {
        if (role.getName() == RoleName.LEADER) {
            // leader角色不可能会选举超时
            log.warn("node {},current role is leader,ignore election timeout", context.getSelfId());
            return;
        }

        /*
         * 对于follower节点来说，是发起选举
         * 对于candidate节点来说是再次发起选举
         */

        // 选举term+1
        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();
        log.info("started election");
        // 变成candidate角色
        changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));

        // 发送requestVote消息
        RequestVoteRpc requestVoteRpc = RequestVoteRpc.builder()
                .term(newTerm)
                .candidateId(context.getSelfId())
                .lastLogIndex(0)
                .lastLogTerm(0)
                .build();
        context.getConnector().sendRequestVote(requestVoteRpc, context.getNodeGroup().listEndpointExceptSelf());
    }

    private ElectionTimeoutTask scheduleElectionTimeout() {
        return context.getScheduler().scheduleElectionTimeoutTask(this::scheduleTimeout);
    }

    @Subscribe
    public void onReceiveRequestVoteRpc(RequestVoteRpcMessage requestVoteRpcMessage) {
        context.getTaskExecutor().submit(() -> context.getConnector().replyRequestVote(
                        doProcessRequestVoteRpc(requestVoteRpcMessage),
                        // 发送消息的节点
                        context.findMember(requestVoteRpcMessage.getSourceNodeId()).getEndpoint()
                )
        );
    }


    private RequestVoteResult doProcessRequestVoteRpc(RequestVoteRpcMessage requestVoteRpcMessage) {
        // 如果对方的term比自己小，则不投票并且返回自己的term对象
        RequestVoteRpc rpc = requestVoteRpcMessage.get();
        if (rpc.getTerm() < role.getTerm()) {
            log.debug("term from rpc < current term, don't vote({}<{})", rpc.getTerm(), role.getTerm());
            return new RequestVoteResult(role.getTerm(), false);
        }
        // 此处无条件投票
        boolean voteForCandidate = true;

        // 此处的term比自己大，切换为Follower角色
        if (rpc.getTerm() > role.getTerm()) {
            becomeFollower(rpc.getTerm(), (voteForCandidate ? rpc.getCandidateId() : null), null, true);
            return new RequestVoteResult(rpc.getTerm(), voteForCandidate);
        }

        // 本地的term与消息一致
        switch (role.getName()) {
            case FOLLOWER:
                FollowerNodeRole followerNodeRole = (FollowerNodeRole) role;
                NodeId voteFor = followerNodeRole.getVotedFor();
                /*
                 * 一下两种情况投票
                 * case1. 自己尚未投过票
                 * case2. 自己已经给对方投过票
                 */
                // 投票后需要切换成Follower角色
                if (
                        voteFor == null && voteForCandidate ||
                                Objects.equals(voteFor, rpc.getCandidateId())
                ) {

                    // case1
                    becomeFollower(role.getTerm(), rpc.getCandidateId(), null, true);
                    return new RequestVoteResult(rpc.getTerm(), true);
                }
                return new RequestVoteResult(role.getTerm(), false);
            case CANDIDATE:
                // 自己已经给自己投过票了，所以不会给其他节点投票
            case LEADER:
                return new RequestVoteResult(role.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role [" + role.getName() + "]");
        }
    }


    private void becomeFollower(int term, NodeId voteFor, NodeId leaderId, boolean scheduleElectionTimeout) {
        role.cancelTimeoutOrTask();
        if (leaderId != null && !leaderId.equals(((FollowerNodeRole) role).getLeaderId(context.getSelfId()))) {
            log.info("current leader is {}, term {}", leaderId, term);
        }
        // 重新创建选举超时定时器或者空定时器
        ElectionTimeoutTask electionTimeoutTask = scheduleElectionTimeout ? scheduleElectionTimeout() : ElectionTimeoutTask.NONE;
        changeToRole(new FollowerNodeRole(term, voteFor, leaderId, electionTimeoutTask));
    }

    @Subscribe
    public void onReceiveRequestVoteResult(RequestVoteResult result) {
        context.getTaskExecutor().submit(() -> doProcessRequestVoteResult(result));
    }

    private void doProcessRequestVoteResult(RequestVoteResult result) {
        // 如果对象的term比自己大，则退化为Follower角色
        if (result.getTerm() > role.getTerm()) {
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }
        // 如果自己不是Candidate角色，则忽略
        if (role.getName() != RoleName.CANDIDATE) {
            log.debug("receive request vote result and current role is not candidate,ignore");
            return;
        }
        // 如果对方的term比自己小或者对象没有给自己投票，则忽略
        if (result.getTerm() < role.getTerm() || !result.isVoteGranted()) {
            return;
        }
        // 当前票数
        int currentVotesCount = ((CandidateNodeRole) role).getVotesCount() + 1;
        // 节点数
        int countOfMajor = context.getNodeGroup().getCount();
        log.debug("votes count {}, node count {}", currentVotesCount, countOfMajor);
        // 取消选举超时定时器
        role.cancelTimeoutOrTask();
        if (currentVotesCount > countOfMajor / 2) {
            // 票数过半成为leader
            log.info("become leader,term {}", role.getTerm());
            // resetReplicationStates();
            changeToRole(new LeaderNodeRole(role.getTerm(), scheduleLogReplicationTask()));
            // no-op log
            // context.log().appendEntry(role.getTerm());
        } else {
            // 修改收到的投票数，并重新创建选举超时定时器
            changeToRole(new CandidateNodeRole(role.getTerm(), currentVotesCount, scheduleElectionTimeout()));
        }
    }

    private LogReplicationTask scheduleLogReplicationTask() {
        return context.getScheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    /**
     * 日志复制入口
     */
    private void replicateLog() {
        context.getTaskExecutor().submit(this::doReplicateLog);
    }

    private void doReplicateLog() {
        log.debug("replicate log");
        context.getNodeGroup()
                .listReplicationTarget()
                .forEach(this::doReplicateLogCore);
    }

    private void doReplicateLogCore(GroupMember member) {
        AppendEntriesRpc appendEntriesRpc = AppendEntriesRpc.builder()
                .term(role.getTerm())
                .leaderId(context.getSelfId())
                .prevLogIndex(0)
                .leaderCommit(0)
                .build();
        context.getConnector().sendAppendEntries(appendEntriesRpc, member.getEndpoint());
    }

    @Subscribe
    public void onReceiveAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        context.getTaskExecutor()
                .submit(() -> context.getConnector().replyEntries(
                        doProcessEntriesRpc(rpcMessage),
                        context.findMember(rpcMessage.getSourceNodeId()).getEndpoint()));
    }

    private AppendEntriesResult doProcessEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        AppendEntriesRpc rpc = rpcMessage.get();
        if (rpc.getTerm() < role.getTerm()) {
            // case 1. 如果对方的term比自己小，则回复自己的term
            return new AppendEntriesResult(role.getTerm(), false);
        }
        if (rpc.getTerm() > role.getTerm()) {
            // case 2. 如果对方的term比自己打，则退化为Follower角色
            becomeFollower(rpc.getTerm(), null, rpc.getLeaderId(), true);
            // 追加日志
            return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
        }
        Assert.isTrue(rpc.getTerm() == role.getTerm(), IllegalArgumentException::new);
        switch (role.getName()) {
            case FOLLOWER:
                // case 3. 设置leader并重置选举定时器
                becomeFollower(rpc.getTerm(), ((FollowerNodeRole) role).getVotedFor(), rpc.getLeaderId(), true);
                // 追加日志
                return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
            case CANDIDATE:
                // 如果有两个Candidate角色，并且另外一个Candidate先成了Leader
                // 当前节点退化为Follower角色，并重置选举定时器
                becomeFollower(rpc.getTerm(), null, rpc.getLeaderId(), true);
                // 追加日志
                return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
            case LEADER:
                // leader角色收到AppendEntries消息，打印警告日志
                log.warn("receive append entries rpc from another leader {},ignore", rpc.getLeaderId());
                return new AppendEntriesResult(rpc.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role {" + role.getName() + "]");
        }
    }

    private boolean appendEntries(AppendEntriesRpc rpc) {
        return true;
    }

    @Subscribe
    public void onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        context.getTaskExecutor().submit(() -> doProcessAppendEntriesResult(resultMessage));
    }

    private void doProcessAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        AppendEntriesResult result = resultMessage.get();
        // 如果对方的term比自己的大，则退化为Follower
        if (result.getTerm() > result.getTerm()) {
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }
        // 检查自己的角色
        if (role.getName() != RoleName.LEADER) {
            log.warn("receive append entries result form node {} but current node is not leader,ignore", resultMessage.getSourceNodeId());
        }
    }


}
