package org.ywb.raft.core.eventbus;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.schedule.task.LogReplicationTask;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.meta.GroupMember;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.rpc.msg.*;
import org.ywb.raft.core.support.role.CandidateNodeRole;
import org.ywb.raft.core.support.role.FollowerNodeRole;
import org.ywb.raft.core.support.role.LeaderNodeRole;
import org.ywb.raft.core.utils.Assert;

import java.util.Objects;

import static org.ywb.raft.core.enums.RoleName.*;

/**
 * @author yuwenbo1
 * @date 2021/4/11 9:21 下午 星期日
 * @since 1.0.0
 */
@Slf4j
public class OnReceiveSubScribeImpl implements OnReceiveSubscribe{

    private final Node node;

    private final NodeContext context;

    public OnReceiveSubScribeImpl(Node node) {
        this.node = node;
        this.context = node.getContext();
        // 注册到自己的eventBus
        context.getEventBus().register(this);
    }

    @Override
    @Subscribe
    public void onReceiveRequestVoteRpc(RequestVoteRpcMessage requestVoteRpcMessage) {
        context.getTaskExecutor().submit(() -> context.getConnector().replyRequestVote(
                doProcessRequestVoteRpc(requestVoteRpcMessage),
                // 发送消息的节点
                context.findMember(requestVoteRpcMessage.getSourceNodeId()).getEndpoint()
                )
        );
    }

    @Override
    @Subscribe
    public void onReceiveRequestVoteResult(RequestVoteResult result) {
        context.getTaskExecutor().submit(() -> doProcessRequestVoteResult(result));
    }

    @Override
    @Subscribe
    public void onReceiveAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        context.getTaskExecutor()
                .submit(() -> context.getConnector().replyEntries(
                        doProcessEntriesRpc(rpcMessage),
                        context.findMember(rpcMessage.getSourceNodeId()).getEndpoint()));
    }

    @Override
    @Subscribe
    public void onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        context.getTaskExecutor().submit(() -> doProcessAppendEntriesResult(resultMessage));
    }

    private RequestVoteResult doProcessRequestVoteRpc(RequestVoteRpcMessage requestVoteRpcMessage) {
        // 如果对方的term比自己小，则不投票并且返回自己的term对象
        RequestVoteRpc rpc = requestVoteRpcMessage.get();
        if (rpc.getTerm() < node.getRole().getTerm()) {
            log.debug("term from rpc < current term, don't vote({}<{})", rpc.getTerm(), node.getRole().getTerm());
            return new RequestVoteResult(node.getRole().getTerm(), false);
        }
        // 此处无条件投票
        boolean voteForCandidate = true;

        // 此处的term比自己大，切换为Follower角色
        if (rpc.getTerm() > node.getRole().getTerm()) {
            becomeFollower(rpc.getTerm(), (voteForCandidate ? rpc.getCandidateId() : null), null, true);
            return new RequestVoteResult(rpc.getTerm(), voteForCandidate);
        }

        // 本地的term与消息一致
        switch (node.getRole().getName()) {
            case FOLLOWER:
                FollowerNodeRole followerNodeRole = (FollowerNodeRole) node.getRole();
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
                    becomeFollower(node.getRole().getTerm(), rpc.getCandidateId(), null, true);
                    return new RequestVoteResult(rpc.getTerm(), true);
                }
                return new RequestVoteResult(node.getRole().getTerm(), false);
            case CANDIDATE:
                // 自己已经给自己投过票了，所以不会给其他节点投票
            case LEADER:
                return new RequestVoteResult(node.getRole().getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role [" + node.getRole().getName() + "]");
        }
    }


    private void becomeFollower(int term, NodeId voteFor, NodeId leaderId, boolean scheduleElectionTimeout) {
        node.getRole().cancelTimeoutOrTask();
        if (leaderId != null && !leaderId.equals(((FollowerNodeRole) node.getRole()).getLeaderId(context.getSelfId()))) {
            log.info("current leader is {}, term {}", leaderId, term);
        }
        // 重新创建选举超时定时器或者空定时器
        ElectionTimeoutTask electionTimeoutTask = scheduleElectionTimeout ? node.scheduleElectionTimeout() : ElectionTimeoutTask.NONE;
        node.changeToRole(new FollowerNodeRole(term, voteFor, leaderId, electionTimeoutTask));
    }

    private void doProcessRequestVoteResult(RequestVoteResult result) {
        // 如果对象的term比自己大，则退化为Follower角色
        if (result.getTerm() > node.getRole().getTerm()) {
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }
        // 如果自己不是Candidate角色，则忽略
        if (node.getRole().getName() != CANDIDATE) {
            log.debug("receive request vote result and current role is not candidate,ignore");
            return;
        }
        // 如果对方的term比自己小或者对方没有给自己投票，则忽略
        if (result.getTerm() < node.getRole().getTerm() || !result.isVoteGranted()) {
            return;
        }
        // 当前票数
        int currentVotesCount = ((CandidateNodeRole) node.getRole()).getVotesCount() + 1;
        // 节点数
        int countOfMajor = context.getNodeGroup().getCount();
        log.debug("votes count {}, node count {}", currentVotesCount, countOfMajor);
        // 取消选举超时定时器
        node.getRole().cancelTimeoutOrTask();
        if (currentVotesCount > countOfMajor / 2) {
            // 票数过半成为leader
            log.info("become leader,term {}", node.getRole().getTerm());
            // resetReplicationStates();
            node.changeToRole(new LeaderNodeRole(node.getRole().getTerm(), scheduleLogReplicationTask()));
            // no-op log
            // context.log().appendEntry(role.getTerm());
        } else {
            // 修改收到的投票数，并重新创建选举超时定时器
            node.changeToRole(new CandidateNodeRole(node.getRole().getTerm(), currentVotesCount, node.scheduleElectionTimeout()));
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
                .term(node.getRole().getTerm())
                .leaderId(context.getSelfId())
                .prevLogIndex(0)
                .leaderCommit(0)
                .build();
        context.getConnector().sendAppendEntries(appendEntriesRpc, member.getEndpoint());
    }

    private AppendEntriesResult doProcessEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        AppendEntriesRpc rpc = rpcMessage.get();
        if (rpc.getTerm() < node.getRole().getTerm()) {
            // case 1. 如果对方的term比自己小，则回复自己的term
            return new AppendEntriesResult(node.getRole().getTerm(), false);
        }
        if (rpc.getTerm() > node.getRole().getTerm()) {
            // case 2. 如果对方的term比自己大，则退化为Follower角色
            becomeFollower(rpc.getTerm(), null, rpc.getLeaderId(), true);
            // 追加日志
            return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
        }
        Assert.isTrue(rpc.getTerm() == node.getRole().getTerm(), IllegalArgumentException::new);
        switch (node.getRole().getName()) {
            case FOLLOWER:
                // case 3. 设置leader并重置选举定时器
                becomeFollower(rpc.getTerm(), ((FollowerNodeRole) node.getRole()).getVotedFor(), rpc.getLeaderId(), true);
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
                throw new IllegalStateException("unexpected node role {" + node.getRole().getName() + "]");
        }
    }

    private boolean appendEntries(AppendEntriesRpc rpc) {
        return true;
    }

    private void doProcessAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        AppendEntriesResult result = resultMessage.get();
        // 如果对方的term比自己的大，则退化为Follower
        if (result.getTerm() > result.getTerm()) {
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }
        // 检查自己的角色
        if (node.getRole().getName() != LEADER) {
            log.warn("receive append entries result form node {} but current node is not leader,ignore", resultMessage.getSourceNodeId());
        }
    }
}
