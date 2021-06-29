package org.ywb.raft.kvstore;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.core.node.NodeBuilder;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Assert;
import org.ywb.raft.kvstore.config.RaftConfig;
import org.ywb.raft.kvstore.config.ServerConfig;
import org.ywb.raft.kvstore.support.YamlConfigUtils;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.ywb.raft.kvstore.support.KVConstants.RAFT_MODE.*;

/**
 * @author yuwenbo1
 * @date 2021/6/10 7:24 上午 星期四
 * @since 1.0.0
 */
@Slf4j
public class CommandLineServerLauncher {

    private Server server;

    public void start() {
        ServerConfig serverConfig = YamlConfigUtils.load(CommandLineServerLauncher.class.getClassLoader().getResourceAsStream("raftConfig.yml"), ServerConfig.class);
        try {
            RaftConfig raftConfig = serverConfig.getRaft();
            switch (raftConfig.getMode()) {
                case STANDBY:
                    startAsStandaloneOrStandBy(raftConfig, true);
                    break;
                case STANDALONE:
                    startAsStandaloneOrStandBy(raftConfig, false);
                    break;
                case MODE_GROUP_MEMBER:
                    startAsGroupMember(raftConfig);
                    break;
                default:
                    throw new IllegalArgumentException("illegal mode [ " + raftConfig.getMode() + "]");
            }
        } catch (Exception e) {
            System.err.println(Throwables.getStackTraceAsString(e));
        }
    }

    private void startAsGroupMember(RaftConfig raftConfig) throws Exception {
        Set<NodeEndpoint> nodeEndpoints = raftConfig.getNodeEndpoints();
        Assert.nonNull(nodeEndpoints, "group-config required");
        // 集群配置
        // 节点ID
        String rawNodeId = raftConfig.getNodeId();
        // 上层服务端口
        int portService = raftConfig.getServicePort();
        Node node = new NodeBuilder(nodeEndpoints, new NodeId(rawNodeId))
                .setDataDir(raftConfig.getDataDir())
                .build();
        Server server = new Server(node, portService);
        log.info("start as group member, group config {}, id {}, port service {}", nodeEndpoints, rawNodeId, portService);
        startServer(server);
    }

    private void startServer(Server server) throws Exception {
        this.server = server;
        this.server.start();
        new CountDownLatch(1).await();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(this::stopServer, "shutdown"));
    }

    private void stopServer() {
        try {
            this.server.close();
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

//    private NodeEndpoint parseNodeEndpoint(String rawNodeEndpoint) {
//        String[] pieces = rawNodeEndpoint.split(",");
//        if (pieces.length != 3) {
//            throw new IllegalArgumentException("illegal node endpoint [ " + rawNodeEndpoint + " ]");
//        }
//        String nodeId = pieces[0];
//        String host = pieces[1];
//        int port;
//        port = Integer.parseInt(pieces[2]);
//        return new NodeEndpoint(nodeId, host, port);
//    }

    private void startAsStandaloneOrStandBy(RaftConfig raftConfig, boolean standby) {
        // todo
    }
}
