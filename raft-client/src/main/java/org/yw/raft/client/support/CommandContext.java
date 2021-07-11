package org.yw.raft.client.support;

import org.yw.raft.client.KvClient;
import org.yw.raft.client.rpc.KvChannel;
import org.yw.raft.client.rpc.ServerRouter;
import org.ywb.raft.core.support.meta.Address;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Map;

/**
 * @author yuwenbo1
 * @date 2021/7/11 10:49 上午 星期日
 * @since 1.0.0
 */
public class CommandContext {

    private final Map<NodeId, Address> serverMap;

    private KvClient client;

    private boolean running = false;

    public CommandContext(Map<NodeId, Address> serverMap) {
        this.serverMap = serverMap;
        this.client = new KvClient(buildServerRouter(serverMap));
    }

    private ServerRouter buildServerRouter(Map<NodeId, Address> serverMap) {
        ServerRouter router = new ServerRouter();
        serverMap.forEach((nodeId, address) -> {
            router.add(nodeId, new KvChannel(address.getHost(), address.getPort()));
        });
        return router;
    }


    public void setLeaderId(NodeId leaderId) {
        client.getServerRouter().setLeaderId(leaderId);
    }
}
