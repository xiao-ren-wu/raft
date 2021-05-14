package org.ywb.raft.core.rpc;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.exceptions.ChannelConnectException;
import org.ywb.raft.core.exceptions.ConnectorException;
import org.ywb.raft.core.rpc.codec.Decoder;
import org.ywb.raft.core.rpc.codec.Encoder;
import org.ywb.raft.core.rpc.handler.ToRemoteHandler;
import org.ywb.raft.core.support.meta.Address;
import org.ywb.raft.core.support.meta.NodeId;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author yuwenbo1
 * @date 2021/5/11 8:11 上午 星期二
 * @since 1.0.0
 */
@Slf4j
@ThreadSafe
@SuppressWarnings("all")
public class OutboundChannelGroup {

    private final EventLoopGroup workerGroup;

    private final EventBus eventBus;

    private final NodeId selfNodeId;

    private final ConcurrentMap<NodeId, Future<NioChannel>> channelMap = new ConcurrentHashMap<>();

    public OutboundChannelGroup(EventLoopGroup workerGroup, EventBus eventBus, NodeId selfNodeId) {
        this.workerGroup = workerGroup;
        this.eventBus = eventBus;
        this.selfNodeId = selfNodeId;
    }

    public NioChannel getOrConnect(NodeId nodeId, Address address) {
        Future<NioChannel> future = channelMap.get(nodeId);
        if (Objects.isNull(future)) {
            FutureTask<NioChannel> newFuture = new FutureTask<>(() -> connect(nodeId, address));
            future = channelMap.putIfAbsent(nodeId, newFuture);
            if (Objects.isNull(future)) {
                future = newFuture;
                newFuture.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            channelMap.remove(nodeId);
            if (e instanceof ExecutionException) {
                Throwable cause = e.getCause();
                if (cause instanceof ConnectorException) {
                    throw new ChannelConnectException("failed to get channel to node " + nodeId + ", cause " + cause.getMessage(), cause);
                }
            }
            throw new ChannelException("failed to get channel to node " + nodeId, e);
        }
    }

    private NioChannel connect(NodeId nodeId, Address address) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new Decoder())
                                .addLast(new Encoder())
                                .addLast(new ToRemoteHandler(eventBus, nodeId, selfNodeId));
                    }
                });
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort()).sync();
        if (!future.isSuccess()) {
            throw new ChannelException("failed to connect", future.cause());
        }
        log.debug("chanel OUTBOUND-{} connected", nodeId);
        Channel nettyChannel = future.channel();
        // 链接关闭后从组里删除
        nettyChannel.closeFuture()
                .addListener((ChannelFutureListener) cf -> {
                    log.debug("channel OUTBOUND-{} disconnected", nodeId);
                    channelMap.remove(nodeId);
                });
        return new NioChannel(nettyChannel);
    }

    public void closeAll() {
        log.debug("close all outbound channels");
        channelMap.forEach((nodeId, nioChannelFuture) -> {
            try {
                nioChannelFuture.get().close();
            } catch (Exception e) {
                log.warn("failed to close " + nodeId);
            }
        });
    }
}
