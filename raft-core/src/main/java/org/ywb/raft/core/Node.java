package org.ywb.raft.core;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:22 上午 星期四
 * @since 1.0.0
 */
public interface Node {

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     *
     * @throws InterruptedException e
     */
    void stop() throws InterruptedException;
}
