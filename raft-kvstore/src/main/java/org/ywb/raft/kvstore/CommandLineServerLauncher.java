package org.ywb.raft.kvstore;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.core.node.NodeBuilder;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ywb.raft.kvstore.support.KVConstants.RAFT_MODE.*;

/**
 * @author yuwenbo1
 * @date 2021/6/10 7:24 上午 星期四
 * @since 1.0.0
 */
@Slf4j
public class CommandLineServerLauncher {

    private Server server;

    public void start(String[] args) {
        Options options = AllOptionals.getOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("raft-kvstore [OPTION]...", options);
            return;
        }
        DefaultParser parser = new DefaultParser();
        CommandLine cmdLine;
        try {
            cmdLine = parser.parse(options, args);
            String raftMode = cmdLine.getOptionValue('m', STANDALONE);
            switch (raftMode) {
                case STANDBY:
                    startAsStandaloneOrStandBy(cmdLine, true);
                    break;
                case STANDALONE:
                    startAsStandaloneOrStandBy(cmdLine, false);
                    break;
                case MODE_GROUP_MEMBER:
                    startAsGroupMember(cmdLine);
                    break;
                default:
                    throw new IllegalArgumentException("illegal mode [ " + raftMode + "]");
            }
        } catch (Exception e) {
            System.err.println(Throwables.getStackTraceAsString(e));
        }
    }

    private void startAsGroupMember(CommandLine cmdLine) throws Exception {
        if (!cmdLine.hasOption("gc")) {
            throw new IllegalArgumentException("group-config required");
        }
        // 集群配置
        String[] rawGroupConfig = cmdLine.getOptionValues("gc");
        // 节点ID
        String rawNodeId = cmdLine.getOptionValue("i");
        // 上层服务端口
        int portService = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();
        // 解析集群配置
        Set<NodeEndpoint> nodeEndpoints = Stream.of(rawGroupConfig)
                .map(this::parseNodeEndpoint)
                .collect(Collectors.toSet());
        Node node = new NodeBuilder(nodeEndpoints, new NodeId(rawNodeId))
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        Server server = new Server(node, portService);
        log.info("start as group member, group config {}, id {}, port service {}", nodeEndpoints, rawNodeId, portService);
        startServer(server);
    }

    private void startServer(Server server) throws Exception {
        this.server = server;
        this.server.start();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(this::stopServer, "shutdown"));
    }

    private void stopServer() {
        try {
            this.server.close();
        } catch (Exception e) {
            System.err.println(Throwables.getStackTraceAsString(e));
        }
    }

    private NodeEndpoint parseNodeEndpoint(String rawNodeEndpoint) {
        String[] pieces = rawNodeEndpoint.split(",");
        if (pieces.length != 3) {
            throw new IllegalArgumentException("illegal node endpoint [ " + rawNodeEndpoint + " ]");
        }
        String nodeId = pieces[0];
        String host = pieces[1];
        int port;
        port = Integer.parseInt(pieces[2]);
        return new NodeEndpoint(nodeId, host, port);
    }

    private void startAsStandaloneOrStandBy(CommandLine cmdLine, boolean standby) {
        // todo
    }
}
