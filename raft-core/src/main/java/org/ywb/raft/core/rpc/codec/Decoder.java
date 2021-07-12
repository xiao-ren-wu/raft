package org.ywb.raft.core.rpc.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.ywb.codec.ProtocolUtils;
import org.ywb.codec.protocol.Message;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryFactory;
import org.ywb.raft.core.proto.Protos;
import org.ywb.raft.core.rpc.msg.AppendEntriesResult;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.ywb.codec.consts.MessageConstants.*;

/**
 * @author yuwenbo1
 * @date 2021/5/10 10:51 下午 星期一
 * @since 1.0.0
 */
@Slf4j
public class Decoder extends ByteToMessageDecoder {

    @Override
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        Message message = ProtocolUtils.read(in);
        if (Objects.isNull(message)) {
            return;
        }
        byte[] payload = message.getPayload();
        int msgType = message.getHeader().getMessageType();
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
            case MSG_TYPE_REQUEST_VOTE_RESULT:
                Protos.RequestVoteResult requestVoteResult = Protos.RequestVoteResult.parseFrom(payload);
                RequestVoteResult voteResult = RequestVoteResult.builder()
                        .voteGranted(requestVoteResult.getVoteGranted())
                        .term(requestVoteResult.getTerm())
                        .build();
                out.add(voteResult);
                break;
            case MSG_TYPE_APPEND_ENTRIES_RPC:
                    Protos.AppendEntriesRpc appendEntriesRpcProto = Protos.AppendEntriesRpc.parseFrom(payload);
                    AppendEntriesRpc entriesRpc = AppendEntriesRpc.builder()
                            .lastEntryIndex(appendEntriesRpcProto.getLastEntryIndex())
                            .term(appendEntriesRpcProto.getTerm())
                            .entries(proto2Obj(appendEntriesRpcProto.getEntriesList()))
                            .leaderCommit(appendEntriesRpcProto.getLeaderCommit())
                            .leaderId(NodeId.of(appendEntriesRpcProto.getLeaderId()))
                            .prevLogIndex(appendEntriesRpcProto.getPrevLogIndex())
                            .build();
                    out.add(entriesRpc);
                break;
            case MSG_TYPE_APPEND_ENTRIES_RESULT:
                Protos.AppendEntriesResult appendEntriesResult = Protos.AppendEntriesResult.parseFrom(payload);
                AppendEntriesResult entriesResult = AppendEntriesResult.builder()
                        .success(appendEntriesResult.getSuccess())
                        .term(appendEntriesResult.getTerm())
                        .build();
                out.add(entriesResult);
                break;
            default:
                throw new IllegalArgumentException("no handler");
        }
    }

    private List<Entry> proto2Obj(List<Protos.AppendEntriesRpc.Entry> entriesList) {
        if (entriesList.isEmpty()) {
            return new ArrayList<>();
        }
        return entriesList.stream()
                .map(EntryFactory::proto2Entry).collect(Collectors.toList());
    }
}
