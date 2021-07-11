package org.yw.raft.client;

import lombok.Getter;
import org.yw.raft.client.message.GetCommand;
import org.yw.raft.client.message.SetCommand;
import org.yw.raft.client.rpc.ServerRouter;

/**
 * @author yuwenbo1
 * @date 2021/7/11 10:50 上午 星期日
 * @since 1.0.0
 */
public class KvClient {

    @Getter
    private final ServerRouter serverRouter;

    public KvClient(ServerRouter serverRouter) {
        this.serverRouter = serverRouter;
    }

//    public void addNode(String nodeId, String host, int port) {
//        serverRouter.send(new AddNodeCommand(nodeId, host, port));
//    }

    public void set(String key, byte[] val) {
        serverRouter.send(new SetCommand(key, val));
    }

    public byte[] get(String key) {
        return (byte[]) serverRouter.send(new GetCommand(key));
    }

}
