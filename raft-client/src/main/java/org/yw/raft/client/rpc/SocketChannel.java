package org.yw.raft.client.rpc;

/**
 * @author yuwenbo1
 * @date 2021/7/3 3:26 下午 星期六
 * @since 1.0.0
 */
public class SocketChannel {

    private final String host;

    private final int port;

    public SocketChannel(String host, int port) {
        this.host = host;
        this.port = port;
    }

}
