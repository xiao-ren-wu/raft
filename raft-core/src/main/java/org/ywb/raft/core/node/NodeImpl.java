package org.ywb.raft.core.node;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.exceptions.NotLeaderException;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.node.support.RoleNameAndLeaderId;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.statemachine.StateMachine;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.meta.GroupMember;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.role.AbstractNodeRole;
import org.ywb.raft.core.support.role.CandidateNodeRole;
import org.ywb.raft.core.support.role.FollowerNodeRole;
import org.ywb.raft.core.utils.Assert;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:23 上午 星期四
 * @since 1.0.0
 */
@Slf4j
public class NodeImpl implements Node {

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

    private StateMachine stateMachine;

    public NodeImpl(NodeContext context) {
        this.context = context;
    }

    @Override
    public void start() {
        if (started) {
            return;
        }
        // 初始化连接器
        context.getConnector().initialize();
        // 启动时为follower
        NodeStore store = context.getNodeStore();
        changeToRole(new FollowerNodeRole(store.getTerm(), store.getVotedFor(), null, scheduleElectionTimeout()));
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

    @Override
    public NodeContext getContext() {
        return this.context;
    }

    @Override
    public AbstractNodeRole getRole() {
        return this.role;
    }

    @Override
    public void changeToRole(AbstractNodeRole nodeRole) {
        log.debug("node {},role state changed -> {}", context.getSelfId(), nodeRole);
        NodeStore store = context.getNodeStore();
        store.setTerm(nodeRole.getTerm());
        if (nodeRole.getName() == RoleName.FOLLOWER) {
            store.setVotedFor(((FollowerNodeRole) nodeRole).getVotedFor());
        }
        this.role = nodeRole;
    }

    @Override
    public void registerStateMachine(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    public void appendLog(byte[] commandBytes) {
        Assert.nonNull(commandBytes);
        assertLeader();
        context.getTaskExecutor().submit(() -> {
                    context.getLog().appendEntry(role.getTerm(), commandBytes);
                    doReplicateLog();
                });
    }

    @Override
    public void doReplicateLog() {
        int nextIndex = context.getLog().getNextIndex();
        EntryMeta lastEntryMeta = context.getLog().getLastEntryMeta();
        int index = lastEntryMeta.getIndex();
        int maxEntries = nextIndex - index;
        log.info(".........maxEntries {},index{} ,nextIndex {}", maxEntries, index, nextIndex);
        context.getNodeGroup()
                .listReplicationTarget()
                .forEach(groupMember -> doReplicateLogCore(groupMember, nextIndex, maxEntries));
    }

    @Override
    public RoleNameAndLeaderId getRoleNameAndLeaderId() {
        return role.getNameAndLeaderId(context.getSelfId());
    }

    private void doReplicateLogCore(GroupMember member, int nextIndex, int maxEntries) {
        log.info("next index{}", member.getNextIndex());
        AppendEntriesRpc appendEntriesRpc =
                context.getLog().createAppendEntriesRpc(role.getTerm(), context.getSelfId(), nextIndex, maxEntries);
        context.getConnector().sendAppendEntries(appendEntriesRpc, member.getEndpoint());
    }

    private void assertLeader() {
        RoleNameAndLeaderId result = role.getNameAndLeaderId(context.getSelfId());
        if (result.getRoleName() == RoleName.LEADER) {
            return;
        }
        NodeEndpoint endpoint = result.getLeaderNodeId() != null ? context.getNodeGroup().findGroupMember(result.getLeaderNodeId()).getEndpoint() : null;
        throw new NotLeaderException(result.getRoleName(), endpoint);
    }

    @Override
    public ElectionTimeoutTask scheduleElectionTimeout() {
        return context.getScheduler().scheduleElectionTimeoutTask(
                () -> context.getTaskExecutor().submit(this::doProcessElectionTimeout)
        );
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
        EntryMeta lastEntryMeta = context.getLog().getLastEntryMeta();
        // 发送requestVote消息
        RequestVoteRpc requestVoteRpc = RequestVoteRpc.builder()
                .term(newTerm)
                .candidateId(context.getSelfId())
                .lastLogIndex(lastEntryMeta.getIndex())
                .lastLogTerm(lastEntryMeta.getTerm())
                .build();
        context.getConnector().sendRequestVote(requestVoteRpc, context.getNodeGroup().listEndpointExceptSelf());
    }
}
