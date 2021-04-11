package org.ywb.raft.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.eventbus.OnReceiveSubScribeImpl;
import org.ywb.raft.core.eventbus.OnReceiveSubscribe;
import org.ywb.raft.core.node.NodeBuilder;
import org.ywb.raft.core.node.NodeImpl;
import org.ywb.raft.core.rpc.MockConnector;
import org.ywb.raft.core.schedule.NullScheduler;
import org.ywb.raft.core.support.DirectThreadTaskExecutor;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.rpc.msg.*;
import org.ywb.raft.core.support.role.CandidateNodeRole;
import org.ywb.raft.core.support.role.FollowerNodeRole;
import org.ywb.raft.core.support.role.LeaderNodeRole;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yuwenbo1
 * @date 2021/4/11 1:31 下午 星期日
 * @since 1.0.0
 */
public class NodeImplTest {

    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... endpoints) {
        return new NodeBuilder(Arrays.asList(endpoints), selfId)
                .setScheduler(new NullScheduler())
                .setConnector(new MockConnector())
                .setTaskExecutor(new DirectThreadTaskExecutor());
    }

    @Test
    public void testStart() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"), new NodeEndpoint("A", "localhost", 2333)).build();
        node.start();
        FollowerNodeRole followerNodeRole = (FollowerNodeRole) node.getRole();
        Assertions.assertEquals(0, followerNodeRole.getTerm());
        Assertions.assertNull(followerNodeRole.getVotedFor());
    }

    @Test
    public void testElectionTimeoutFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();

        OnReceiveSubscribe onReceiveSubScribe = new OnReceiveSubScribeImpl(node);

        node.start();
        // 直接执行选举任务
        node.electionTimeout();
        CandidateNodeRole candidateNodeRole = (CandidateNodeRole) node.getRole();

        Assertions.assertEquals(1, candidateNodeRole.getTerm());
        Assertions.assertEquals(1, candidateNodeRole.getVotesCount());

        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();
        RequestVoteRpc rpc = (RequestVoteRpc) mockConnector.getRPC();

        Assertions.assertEquals(1, rpc.getTerm());
        Assertions.assertEquals(NodeId.of("A"), rpc.getCandidateId());
        Assertions.assertEquals(0, rpc.getLastLogIndex());
        Assertions.assertEquals(0, rpc.getLastLogTerm());

    }

    @Test
    public void tetOnReceiveRequestVoteRpcFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();

        OnReceiveSubscribe onReceiveSubScribe = new OnReceiveSubScribeImpl(node);


        node.start();
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(1);
        rpc.setCandidateId(NodeId.of("C"));
        rpc.setLastLogIndex(0);
        rpc.setLastLogTerm(0);

        onReceiveSubScribe.onReceiveRequestVoteRpc(new RequestVoteRpcMessage(rpc, NodeId.of("C")));

        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();
        RequestVoteResult result = (RequestVoteResult) mockConnector.getResult();

        Assertions.assertEquals(1, result.getTerm());
        Assertions.assertTrue(result.isVoteGranted());
        Assertions.assertEquals(NodeId.of("C"), ((FollowerNodeRole) node.getRole()).getVotedFor());
    }

    @Test
    public void testReceiveRequestVoteResult() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();

        OnReceiveSubscribe onReceiveSubScribe = new OnReceiveSubScribeImpl(node);

        node.start();
        node.electionTimeout();
        onReceiveSubScribe.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        LeaderNodeRole role = (LeaderNodeRole) node.getRole();
        Assertions.assertEquals(1, role.getTerm());
    }

    @Test
    public void testReplicateLog() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        OnReceiveSubScribeImpl onReceiveSubScribe = new OnReceiveSubScribeImpl(node);

        node.start();
        node.electionTimeout();
        onReceiveSubScribe.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        // todo 测试时需要将该方法的访问权限置换成public
//        onReceiveSubScribe.replicateLog();

        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();

        Assertions.assertEquals(3, mockConnector.getMessageCount());

        List<MockConnector.Message> messages = mockConnector.getMessages();
        Set<NodeId> destinationNodeIds = messages.subList(1, 3)
                .stream()
                .map(MockConnector.Message::getDestinationNodeId)
                .collect(Collectors.toSet());

        Assertions.assertEquals(2, destinationNodeIds.size());
        Assertions.assertTrue(destinationNodeIds.contains(NodeId.of("B")));
        Assertions.assertTrue(destinationNodeIds.contains(NodeId.of("C")));
        AppendEntriesRpc rpc = (AppendEntriesRpc) messages.get(2).getRpc();
        Assertions.assertEquals(1, rpc.getTerm());

    }

    @Test
    public void testOnReceiveAppendEntriesRpcFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(1);
        rpc.setLeaderId(NodeId.of("B"));
        OnReceiveSubScribeImpl onReceiveSubScribe = new OnReceiveSubScribeImpl(node);

        onReceiveSubScribe.onReceiveAppendEntriesRpc(new AppendEntriesRpcMessage(NodeId.of("B"), rpc));
        MockConnector mockConnector = (MockConnector) node.getContext().getConnector();
        AppendEntriesResult result = (AppendEntriesResult) mockConnector.getResult();

        Assertions.assertEquals(1, result.getTerm());
        Assertions.assertTrue(result.isSuccess());
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assertions.assertEquals(1, role.getTerm());
        Assertions.assertEquals(NodeId.of("B"), role.getLeaderId());
    }

    @Test
    public void testOnReceiveAppendEntriesNormal() {
        NodeImpl node = (NodeImpl) newNodeBuilder(NodeId.of("A"),
                new NodeEndpoint("A", "localhost", 2333),
                new NodeEndpoint("B", "localhost", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        OnReceiveSubScribeImpl onReceiveSubScribe = new OnReceiveSubScribeImpl(node);

        node.start();
        node.electionTimeout();
        onReceiveSubScribe.onReceiveRequestVoteResult(new RequestVoteResult(1, true));
        onReceiveSubScribe.onReceiveAppendEntriesResult(new AppendEntriesResultMessage(
                new AppendEntriesResult(1, true),
                NodeId.of("B")
        ));
    }
}
