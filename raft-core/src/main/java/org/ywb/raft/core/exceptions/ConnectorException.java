package org.ywb.raft.core.exceptions;

/**
 * @author yuwenbo1
 * @date 2021/5/10 10:09 下午 星期一
 * @since 1.0.0
 */
public class ConnectorException extends Throwable {
    public ConnectorException(String failed_to_bind_port, InterruptedException e) {
    }
}
