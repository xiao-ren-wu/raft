package org.ywb.raft.core.rpc.handler;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.rpc.NioChannel;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/5/12 7:52 上午 星期三
 * @since 1.0.0
 */
@Slf4j
public class ToRemoteHandler extends AbstractHandler {

    private final NodeId selfNodeId;

    public ToRemoteHandler(EventBus eventBus, NodeId remoteId, NodeId selfNodeId) {
        super(eventBus);
        this.remoteId = remoteId;
        this.selfNodeId = selfNodeId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(selfNodeId);
        channel = new NioChannel(ctx.channel());
    }
}
