package org.ywb.raft.core.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.ywb.raft.core.enums.MessageConstants;
import org.ywb.raft.core.proto.Protos;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.nio.charset.StandardCharsets;

import static org.ywb.raft.core.enums.MessageConstants.MAGIC;
import static org.ywb.raft.core.enums.MessageConstants.VERSION;

/**
 * @author yuwenbo1
 * @date 2021/5/10 10:18 下午 星期一
 * @since 1.0.0
 * 协议定义
 * +----------------+----------+-----+---------+
 * |  magic(0xBCDE) |  version | type| bodyLen |
 * +----------------+----------+-----+---------+
 * |               PAYLOAD                     |
 * +-------------------------------------------+
 */
public class Encoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof NodeId) {
            this.writeMessage(out, MessageConstants.MSG_TYPE_NODE_ID, ((NodeId) msg).getVal().getBytes(StandardCharsets.UTF_8));
        } else if (msg instanceof RequestVoteRpc) {
            RequestVoteRpc requestVoteRpc = (RequestVoteRpc) msg;
            Protos.RequestVoteRpc protoRpc = Protos.RequestVoteRpc.newBuilder()
                    .setTerm(requestVoteRpc.getTerm())
                    .setCandidateId(requestVoteRpc.getCandidateId().getVal())
                    .setLastLogIndex(requestVoteRpc.getLastLogIndex())
                    .setLastLogTerm(requestVoteRpc.getLastLogTerm())
                    .build();
            this.writeMessage(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC, protoRpc.toByteArray());
        }
    }

    private void writeMessage(ByteBuf out, int msgType, byte[] bytes) {
        out.writeInt(MAGIC);
        out.writeInt(VERSION);
        out.writeInt(msgType);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
