package org.ywb.raft.core.exceptions;

/**
 * @author yuwenbo1
 * @date 2021/5/12 7:39 上午 星期三
 * @since 1.0.0
 */
public class ChannelConnectException extends RuntimeException {
    public ChannelConnectException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
