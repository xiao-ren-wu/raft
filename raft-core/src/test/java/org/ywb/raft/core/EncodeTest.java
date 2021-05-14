package org.ywb.raft.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.enums.MessageConstants;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.core.proto.Protos;
import org.ywb.raft.core.rpc.codec.Encoder;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yuwenbo1
 * @date 2021/5/14 8:14 上午 星期五
 * @since 1.0.0
 */
public class EncodeTest {

    @Test
    public void testNodeId() throws Exception {
        Encoder encoder = new Encoder();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, NodeId.of("A"), buf);
        buf.skipBytes(8);
        assertEquals(MessageConstants.MSG_TYPE_NODE_ID, buf.readInt());
        assertEquals(1, buf.readInt());
        assertEquals((byte) 'A', buf.readByte());
    }

    @Test
    public void testRequestVoteRpc() throws Exception {
        Encoder encoder = new Encoder();
        ByteBuf buffer = Unpooled.buffer();
        RequestVoteRpc requestVoteRpc = new RequestVoteRpc();
        requestVoteRpc.setLastLogIndex(2);
        requestVoteRpc.setLastLogTerm(1);
        requestVoteRpc.setTerm(2);
        requestVoteRpc.setCandidateId(NodeId.of("A"));
        encoder.encode(null,requestVoteRpc,buffer);
        buffer.skipBytes(8);
        assertEquals(MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC,buffer.readInt());
        buffer.readInt();
        Protos.RequestVoteRpc requestVoteRpc1 = Protos.RequestVoteRpc.parseFrom(new ByteBufInputStream(buffer));
        assertEquals(requestVoteRpc.getLastLogIndex(),requestVoteRpc1.getLastLogIndex());
        assertEquals(requestVoteRpc.getTerm(),requestVoteRpc1.getTerm());
        assertEquals(requestVoteRpc.getCandidateId().getVal(),requestVoteRpc1.getCandidateId());
    }
}
