package org.ywb.raft.kvstore;

/**
 * @author yuwenbo1
 * @date 2021/6/27 10:33 下午 星期日
 * @since 1.0.0
 */
public class RaftKvApplication {
    public static void main(String[] args) {
        new CommandLineServerLauncher().start(args);
    }
}
