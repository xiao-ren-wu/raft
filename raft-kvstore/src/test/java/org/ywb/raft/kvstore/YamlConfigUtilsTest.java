package org.ywb.raft.kvstore;

import org.junit.jupiter.api.Test;
import org.ywb.raft.kvstore.config.ServerConfig;
import org.ywb.raft.kvstore.support.YamlConfigUtils;

/**
 * @author yuwenbo1
 * @date 2021/6/29 11:13 下午 星期二
 * @since 1.0.0
 */
public class YamlConfigUtilsTest {
    @Test
    public void testLoad() {
        ServerConfig serverConfig = YamlConfigUtils.load(CommandLineServerLauncher.class.getClassLoader().getResourceAsStream("raftConfig.yml"), ServerConfig.class);
        System.out.println(serverConfig);
    }
}
