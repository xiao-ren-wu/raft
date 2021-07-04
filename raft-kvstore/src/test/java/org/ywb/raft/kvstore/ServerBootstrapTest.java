package org.ywb.raft.kvstore;


import org.junit.jupiter.api.Test;
import org.ywb.raft.kvstore.config.ServerConfig;

import static org.ywb.raft.kvstore.support.YamlConfigUtils.loadResource;

class ServerBootstrapTest {


    @Test
    public void test() {
        ServerConfig serverConfig = loadResource("raftConfig-a.yml", ServerConfig.class);
        new ServerBootstrap().start(serverConfig);
    }

    @Test
    public void test2() {
        ServerConfig serverConfig = loadResource("raftConfig-b.yml", ServerConfig.class);
        new ServerBootstrap().start(serverConfig);
    }

    @Test
    public void test3() {
        ServerConfig serverConfig = loadResource("raftConfig-c.yml", ServerConfig.class);
        new ServerBootstrap().start(serverConfig);
    }
}