package org.ywb.raft.kvstore;

import org.ywb.raft.kvstore.config.ServerConfig;
import org.ywb.raft.kvstore.support.YamlConfigUtils;

/**
 * @author yuwenbo1
 * @date 2021/6/27 10:33 下午 星期日
 * @since 1.0.0
 */
public class RaftKvApplication {
    public static void main(String[] args) {
        new ServerBootstrap()
                .start(YamlConfigUtils.loadResource("raftConfig.yaml", ServerConfig.class));
    }
}
