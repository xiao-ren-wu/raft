package org.yw.raft.client.cmd;

import lombok.Getter;

/**
 * @author yuwenbo1
 * @date 2021/7/11 12:37 下午 星期日
 * @since 1.0.0
 */
@Getter
public class AddNodeCommand {

    private final String nodeId;
    private final String host;
    private final int port;


    public AddNodeCommand(String nodeId, String host, int port) {
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
    }

}
