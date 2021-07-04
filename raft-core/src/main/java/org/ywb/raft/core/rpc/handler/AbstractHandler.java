package org.ywb.raft.core.rpc.handler;

import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.rpc.NettyRaftChannel;
import org.ywb.raft.core.rpc.msg.*;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuwenbo1
 * @date 2021/5/11 7:36 上午 星期二
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public abstract class AbstractHandler extends ChannelDuplexHandler {

    protected final EventBus eventBus;

    private volatile AppendEntriesRpc lastAppendEntiresRpc;

    private static Map<String, AppendEntriesRpc> map = new ConcurrentHashMap<>();

    /**
     * 远程节点id
     */
    NodeId remoteId;

    protected NettyRaftChannel channel;

    public AbstractHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Assert.isTrue(Objects.nonNull(remoteId), () -> new IllegalArgumentException("remote id must not be null"));
        Assert.isTrue(Objects.nonNull(channel), () -> new IllegalArgumentException("rpc channel must not be null"));
        if (msg instanceof RequestVoteRpc) {
            RequestVoteRpc rpc = (RequestVoteRpc) msg;
            eventBus.post(new RequestVoteRpcMessage(rpc, remoteId, channel));
        } else if (msg instanceof RequestVoteResult) {
            eventBus.post(msg);
        } else if (msg instanceof AppendEntriesResult) {
            AppendEntriesResult result = (AppendEntriesResult) msg;
            AppendEntriesRpc lastAppendEntiresRpc = map.remove(remoteId.getVal());
            if (Objects.isNull(lastAppendEntiresRpc)) {
                log.warn("remove id {}, no last append entries rpc", remoteId.getVal());
            } else {
                eventBus.post(new AppendEntriesResultMessage(result, remoteId, lastAppendEntiresRpc));
            }
        } else if (msg instanceof AppendEntriesRpc) {
            AppendEntriesRpc entriesRpc = (AppendEntriesRpc) msg;
            eventBus.post(new AppendEntriesRpcMessage(remoteId, entriesRpc));
        } else {
            throw new IllegalArgumentException(msg.toString());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AppendEntriesRpc) {
            lastAppendEntiresRpc = (AppendEntriesRpc) msg;
            map.put(remoteId.getVal(), lastAppendEntiresRpc);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn(Throwables.getStackTraceAsString(cause));
        ctx.close();
    }
}
