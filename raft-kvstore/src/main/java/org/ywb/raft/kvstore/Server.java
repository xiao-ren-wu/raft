package org.ywb.raft.kvstore;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.kvstore.rpc.codec.Decoder;
import org.ywb.raft.kvstore.rpc.codec.Encoder;
import org.ywb.raft.kvstore.rpc.handler.ServiceHandler;
import org.ywb.raft.kvstore.support.Service;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author yuwenbo1
 * @date 2021/6/1 10:42 下午 星期二
 * @since 1.0.0
 */
@Slf4j
public class Server implements Closeable {

    private final Node node;

    private final int port;

    private final Service service;

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();

    private final NioEventLoopGroup workGroup = new NioEventLoopGroup();

    public Server(Node node, int port) {
        this.node = node;
        this.port = port;
        this.service = new Service(node);
    }

    public void start() throws Exception {
        this.node.start();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(workGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new Encoder())
                                .addLast(new Decoder())
                                .addLast(new ServiceHandler(service));
                    }
                });
        log.info("server started at port {}", this.port);
        serverBootstrap.bind(port);
    }


    @Override
    public void close() throws IOException {
        log.info("stopping server");
        try {
            this.node.stop();
            this.workGroup.shutdownGracefully();
            this.bossGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
