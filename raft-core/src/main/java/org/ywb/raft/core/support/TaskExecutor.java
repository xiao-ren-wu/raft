package org.ywb.raft.core.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:36 下午 星期三
 * @since 1.0.0
 */
public interface TaskExecutor {

    /**
     * 提交任务，任务没有返回值
     *
     * @param task task
     * @return future
     */
    Future<?> submit(Runnable task);

    /**
     * 提交任务，任务有返回值
     *
     * @param task task
     * @param <V>  return type
     * @return V
     */
    <V> Future<V> submit(Callable<V> task);

    /**
     * 关闭任务执行器
     *
     * @throws InterruptedException E
     */
    void shutdown() throws InterruptedException;
}
