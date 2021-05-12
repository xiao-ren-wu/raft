package org.ywb.raft.core.rpc.handler;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.rpc.NioChannel;
import org.ywb.raft.core.rpc.msg.*;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Assert;

import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/5/11 7:36 上午 星期二
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public abstract class AbstractHandler extends ChannelDuplexHandler {

    protected final EventBus eventBus;

    /**
     * 远程节点id
     */
    NodeId remoteId;

    protected NioChannel channel;

    private AppendEntriesRpc lastAppendEntiresRpc;

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
            if (Objects.isNull(lastAppendEntiresRpc)) {
                log.warn("no last append entries rpc");
            } else {
                eventBus.post(new AppendEntriesResultMessage(result, remoteId, lastAppendEntiresRpc));
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AppendEntriesRpc) {
            lastAppendEntiresRpc = (AppendEntriesRpc) msg;
        }
        super.write(ctx, msg, promise);
    }
}
