package org.ywb.raft.kvstore.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.ywb.raft.kvstore.message.GetCommand;
import org.ywb.raft.kvstore.message.SetCommand;
import org.ywb.raft.kvstore.support.CommandRequest;
import org.ywb.raft.kvstore.support.Service;

/**
 * @author yuwenbo1
 * @date 2021/6/1 10:39 下午 星期二
 * @since 1.0.0
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {

    private final Service service;

    public ServiceHandler(Service service) {
        this.service = service;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GetCommand) {
            service.get(new CommandRequest<>((GetCommand) msg, ctx.channel()));
        } else if (msg instanceof SetCommand) {
            service.set(new CommandRequest<>((SetCommand) msg, ctx.channel()));
        }
    }
}
