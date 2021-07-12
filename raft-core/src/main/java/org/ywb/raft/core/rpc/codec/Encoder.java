package org.ywb.raft.core.rpc.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.ywb.codec.ProtocolUtils;
import org.ywb.codec.consts.MessageConstants;
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
import java.util.stream.Collectors;

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
@Slf4j
public class Encoder extends MessageToByteEncoder<Object> {

    @Override
    public void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof NodeId) {
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_NODE_ID, ((NodeId) msg).getVal().getBytes(StandardCharsets.UTF_8));
        } else if (msg instanceof RequestVoteRpc) {
            RequestVoteRpc requestVoteRpc = (RequestVoteRpc) msg;
            Protos.RequestVoteRpc protoRpc = Protos.RequestVoteRpc.newBuilder()
                    .setTerm(requestVoteRpc.getTerm())
                    .setCandidateId(requestVoteRpc.getCandidateId().getVal())
                    .setLastLogIndex(requestVoteRpc.getLastLogIndex())
                    .setLastLogTerm(requestVoteRpc.getLastLogTerm())
                    .build();
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC, protoRpc.toByteArray());
        } else if (msg instanceof RequestVoteResult) {
            RequestVoteResult requestVoteResult = (RequestVoteResult) msg;
            Protos.RequestVoteResult voteResultProto = Protos.RequestVoteResult.newBuilder()
                    .setTerm(requestVoteResult.getTerm())
                    .setVoteGranted(requestVoteResult.isVoteGranted())
                    .build();
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_REQUEST_VOTE_RESULT, voteResultProto.toByteArray());
        } else if (msg instanceof AppendEntriesRpc) {
                Protos.AppendEntriesRpc appendEntriesRpcProto = null;
                AppendEntriesRpc appendEntriesRpc = (AppendEntriesRpc) msg;
                appendEntriesRpcProto = Protos.AppendEntriesRpc
                        .newBuilder()
                        .setTerm(appendEntriesRpc.getTerm())
                        .setLeaderCommit(appendEntriesRpc.getLeaderCommit())
                        .setPrevLogIndex(appendEntriesRpc.getPrevLogIndex())
                        .setPrevLogTerm(appendEntriesRpc.getPrevLogTerm())
                        .setLeaderId(appendEntriesRpc.getLeaderId().getVal())
                        .setLastEntryIndex(appendEntriesRpc.getLastEntryIndex())
                        .addAllEntries(entry2Proto(appendEntriesRpc.getEntries()))
                        .build();
                ProtocolUtils.write(out, MessageConstants.MSG_TYPE_APPEND_ENTRIES_RPC, appendEntriesRpcProto.toByteArray());
        } else if (msg instanceof AppendEntriesResult) {
            AppendEntriesResult appendEntriesResult = (AppendEntriesResult) msg;
            Protos.AppendEntriesResult entriesResultProto = Protos.AppendEntriesResult
                    .newBuilder()
                    .setTerm(appendEntriesResult.getTerm())
                    .setSuccess(appendEntriesResult.isSuccess())
                    .build();
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_APPEND_ENTRIES_RESULT, entriesResultProto.toByteArray());
        } else {
            throw new IllegalArgumentException(msg.toString());
        }
    }

    private Iterable<? extends Protos.AppendEntriesRpc.Entry> entry2Proto(List<Entry> entries) {
        if(entries==null||entries.isEmpty()){
            return new ArrayList<>();
        }
        return entries.stream()
                .map(EntryFactory::entry2Proto)
                .collect(Collectors.toList());
    }
}
