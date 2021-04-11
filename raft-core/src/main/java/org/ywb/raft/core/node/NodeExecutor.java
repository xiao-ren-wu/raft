package org.ywb.raft.core.node;

/**
 * @author yuwenbo1
 * @date 2021/4/11 9:49 下午 星期日
 * @since 1.0.0
 */
public interface NodeExecutor {

    /**
     * 启动
     * 为了防止同步调用，在已经启动的状态下不做任何事情
     * 系统初始化时，注册自己感兴趣的消息，以及初始化RPC组件，切换角色为follower，并设置选举超时
     */
    void start();

    /**
     * 关闭
     * 检查系统是否启动，然后逐个关闭相关组件并设置started为false
     *
     * @throws InterruptedException e
     */
    void stop() throws InterruptedException;
}
