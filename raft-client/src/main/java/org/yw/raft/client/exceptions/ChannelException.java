package org.yw.raft.client.exceptions;

/**
 * @author yuwenbo1
 * @date 2021/7/11 12:18 下午 星期日
 * @since 1.0.0
 */
public class ChannelException extends RuntimeException {
    public ChannelException(String errMsg) {
        super(errMsg);
    }
}
