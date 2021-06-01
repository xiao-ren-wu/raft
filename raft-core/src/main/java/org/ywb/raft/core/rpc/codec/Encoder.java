package org.ywb.raft.core.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.ywb.raft.core.enums.MessageConstants;
import org.ywb.raft.core.proto.Protos;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.nio.charset.StandardCharsets;

import static org.ywb.raft.core.enums.MessageConstants.RAFT_MAGIC;
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
    public void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
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
        } else if (msg instanceof RequestVoteResult) {
            RequestVoteResult requestVoteResult = (RequestVoteResult) msg;
            Protos.RequestVoteResult voteResultProto = Protos.RequestVoteResult.newBuilder()
                    .setTerm(requestVoteResult.getTerm())
                    .setVoteGranted(requestVoteResult.isVoteGranted())
                    .build();
            this.writeMessage(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_RESULT, voteResultProto.toByteArray());
        }
    }

    private void writeMessage(ByteBuf out, int msgType, byte[] bytes) {
        out.writeInt(RAFT_MAGIC);
        out.writeInt(VERSION);
        out.writeInt(msgType);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
