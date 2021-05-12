package org.ywb.raft.core.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.ywb.raft.core.exceptions.MagicCodeErrorException;
import org.ywb.raft.core.proto.Protos;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Assert;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.ywb.raft.core.enums.MessageConstants.*;


/**
 * @author yuwenbo1
 * @date 2021/5/10 10:51 下午 星期一
 * @since 1.0.0
 */
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int availableByte = in.readableBytes();
        // 是否多余16个字节可读，16字节为自定义协议header的长度
        if (availableByte < HEADER_LEN) {
            return;
        }
        int magic = in.readInt();
        Assert.isTrue(MAGIC - magic == 0, () -> new MagicCodeErrorException(magic));
        // skip version
        in.skipBytes(4);
        // msg type
        int msgType = in.readInt();

        // payload len
        int payloadLen = in.readInt();
        if (in.readableBytes() < payloadLen) {
            // 消息未完全可读，回到起始位置
            in.resetReaderIndex();
            return;
        }
        // 消息可读
        byte[] payload = new byte[payloadLen];
        in.readBytes(payload);
        // 根据消息类型进行反序列化
        switch (msgType) {
            case MSG_TYPE_NODE_ID:
                out.add(new NodeId(new String(payload, StandardCharsets.UTF_8)));
                break;
            case MSG_TYPE_REQUEST_VOTE_RPC:
                Protos.RequestVoteRpc requestVoteRpc = Protos.RequestVoteRpc.parseFrom(payload);
                RequestVoteRpc voteRpc = RequestVoteRpc.builder()
                        .candidateId(NodeId.of(requestVoteRpc.getCandidateId()))
                        .lastLogIndex(requestVoteRpc.getLastLogIndex())
                        .lastLogTerm(requestVoteRpc.getLastLogTerm())
                        .term(requestVoteRpc.getTerm())
                        .build();
                out.add(voteRpc);
                break;
            default:
        }

    }
}
