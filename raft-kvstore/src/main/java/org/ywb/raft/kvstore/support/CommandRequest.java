package org.ywb.raft.kvstore.support;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:25 下午 星期一
 * @since 1.0.0
 */
@Slf4j
public class CommandRequest<T> {

    private final T command;

    private final Channel channel;

    public CommandRequest(T command, Channel channel) {
        this.command = command;
        this.channel = channel;
    }

    public void reply(Object response) {
        this.channel.writeAndFlush(response);
    }

    public void addCloseListener(Runnable runnable) {
        this.channel.closeFuture().addListener(future -> runnable.run());
    }

    public T getCommand() {
        return command;
    }




}
