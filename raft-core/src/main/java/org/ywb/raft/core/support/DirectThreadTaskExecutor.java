package org.ywb.raft.core.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:44 下午 星期三
 * @since 1.0.0
 * 同步执行任务
 */
public class DirectThreadTaskExecutor implements TaskExecutor {

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<Void> futureTask = new FutureTask<>(task, null);
        futureTask.run();
        return futureTask;
    }

    @Override
    public <V> Future<V> submit(Callable<V> task) {
        FutureTask<V> futureTask = new FutureTask<>(task);
        futureTask.run();
        return futureTask;
    }

    @Override
    public void shutdown() throws InterruptedException {
        // just do nothing
    }
}
