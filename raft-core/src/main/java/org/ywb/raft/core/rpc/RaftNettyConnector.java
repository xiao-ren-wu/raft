package org.ywb.raft.core.rpc;

import com.google.common.eventbus.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.exceptions.ConnectorException;
import org.ywb.raft.core.rpc.codec.Decoder;
import org.ywb.raft.core.rpc.codec.Encoder;
import org.ywb.raft.core.rpc.handler.FromRemoteHandler;
import org.ywb.raft.core.rpc.msg.AppendEntriesResult;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Collection;

/**
 * @author yuwenbo1
 * @date 2021/5/10 9:47 下午 星期一
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public class RaftNettyConnector implements Connector {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private final EventLoopGroup workerGroup;

    /**
     * 是否和上层服务共享线程池
     */
    private final boolean workerGroupShared;

    private final EventBus eventBus;

    private final int port;

    private final InboundChannelGroup inboundChannelGroup = new InboundChannelGroup();

    private final OutboundChannelGroup outBoundChannelGroup;

    public RaftNettyConnector(
            NioEventLoopGroup workerGroup,
            boolean workerGroupShared,
            NodeId selfNodeId,
            EventBus eventBus,
            int port
    ) {
        this.workerGroup = workerGroup;
        this.workerGroupShared = workerGroupShared;
        this.eventBus = eventBus;
        this.port = port;
        outBoundChannelGroup = new OutboundChannelGroup(workerGroup, eventBus, selfNodeId);
    }

    @Override
    public void initialize() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(workerGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new Decoder())
                                .addLast(new Encoder())
                                .addLast(new FromRemoteHandler(eventBus, inboundChannelGroup));
                    }
                });
        log.debug("node listen on port {}", port);
        try {
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            throw new ConnectorException("failed to bind port", e);
        }
    }

    @Override
    public void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints) {
        destinationEndpoints.forEach(endpoint -> {
            getChannel(endpoint).writeRequestVoteRpc(rpc);
        });
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint) {
        getChannel(destinationEndpoint).writeRequestVoteResult(result);
    }

    @Override
    public void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint) {
        getChannel(destinationEndpoint).writeAppendEntriesRpc(rpc);
    }

    @Override
    public void replyEntries(AppendEntriesResult result, NodeEndpoint destinationEndpoint) {
        getChannel(destinationEndpoint).writeAppendEntriesResult(result);
    }

    @Override
    public void close() {
        log.debug("close connector");
        inboundChannelGroup.closeAll();
        outBoundChannelGroup.closeAll();
        bossGroup.shutdownGracefully();
        if (!workerGroupShared) {
            workerGroup.shutdownGracefully();
        }
    }

    private Channel getChannel(NodeEndpoint endpoint) {
        return outBoundChannelGroup.getOrConnect(endpoint.getNodeId(), endpoint.getAddress());
    }
}
