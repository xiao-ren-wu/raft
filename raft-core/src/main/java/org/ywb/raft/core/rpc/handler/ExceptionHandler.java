package org.ywb.raft.core.rpc.handler;

import com.google.common.base.Throwables;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuwenbo1
 * @date 2021/7/12 10:43 上午 星期一
 * @since 1.0.0
 */
@Slf4j
public class ExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(Throwables.getStackTraceAsString(cause));
    }
}
