package org.ywb.raft.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.rpc.codec.Decoder;
import org.ywb.raft.core.rpc.codec.Encoder;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/5/14 8:26 上午 星期五
 * @since 1.0.0
 */
public class DecodeTest {

    @Test
    public void testNodeId() throws Exception {
        Encoder encoder = new Encoder();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, NodeId.of("A"), buf);
        Decoder decoder = new Decoder();
        ArrayList<Object> list = new ArrayList<>();
        decoder.decode(null,buf,list);
        Assertions.assertEquals(list.get(0),NodeId.of("A"));
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
        Decoder decoder = new Decoder();
        List<Object> list = new ArrayList<>();
        decoder.decode(null,buffer,list);
        RequestVoteRpc o = (RequestVoteRpc)list.get(0);
        Assertions.assertEquals(o,requestVoteRpc);
    }
}
