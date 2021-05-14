package org.ywb.raft.core.rpc.handler;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import org.ywb.raft.core.rpc.InboundChannelGroup;
import org.ywb.raft.core.rpc.NioChannel;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/5/11 7:35 上午 星期二
 * @since 1.0.0
 */
public class FromRemoteHandler extends AbstractHandler {

    private final InboundChannelGroup channelGroup;

    public FromRemoteHandler(EventBus eventBus,InboundChannelGroup channelGroup) {
        super(eventBus);
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NodeId) {
            super.remoteId = (NodeId) msg;
            super.channel = new NioChannel(ctx.channel());
            channelGroup.add(remoteId, channel);
            return;
        }
        super.channelRead(ctx, msg);
    }
}
