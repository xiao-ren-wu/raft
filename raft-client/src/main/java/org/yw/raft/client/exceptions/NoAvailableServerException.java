package org.yw.raft.client.exceptions;

/**
 * @author yuwenbo1
 * @date 2021/7/11 11:07 上午 星期日
 * @since 1.0.0
 */
public class NoAvailableServerException extends RuntimeException {
    public NoAvailableServerException(String errMsg) {
        super(errMsg);
    }

    public NoAvailableServerException() {
        super("no available server");
    }
}
