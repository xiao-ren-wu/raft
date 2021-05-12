package org.ywb.raft.core.rpc;

import io.netty.channel.ChannelException;
import org.ywb.raft.core.rpc.msg.AppendEntriesResult;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;

import javax.annotation.Nonnull;

/**
 * @author yuwenbo1
 * @date 2021/5/11 7:58 上午 星期二
 * @since 1.0.0
 */
public class NioChannel implements Channel {

    private final io.netty.channel.Channel nettyChannel;

    public NioChannel(io.netty.channel.Channel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    public void writeRequestVoteRpc(@Nonnull RequestVoteRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeRequestVoteResult(@Nonnull RequestVoteResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void writeAppendEntriesRpc(@Nonnull AppendEntriesRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeAppendEntriesResult(@Nonnull AppendEntriesResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void close() {
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            throw new ChannelException("fail to close channel", e);
        }
    }

    public io.netty.channel.Channel getDelegate() {
        return nettyChannel;
    }
}
