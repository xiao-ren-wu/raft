package org.ywb.raft.core.support;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:40 下午 星期三
 * @since 1.0.0
 * 异步单线程实现
 */
@Slf4j
public class SingleThreadTaskExecutor implements TaskExecutor {

    private final ExecutorService executorService;

    public SingleThreadTaskExecutor(ThreadFactory threadFactory) {
        executorService = Executors.newSingleThreadExecutor(threadFactory);
    }

    public SingleThreadTaskExecutor(String name) {
        this(r -> new Thread(r, name));
    }

    public SingleThreadTaskExecutor() {
        this(Executors.defaultThreadFactory());
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error(Throwables.getStackTraceAsString(e));
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <V> Future<V> submit(Callable<V> task) {
        return this.executorService.submit(task);
    }

    @Override
    public void shutdown() throws InterruptedException {
        this.executorService.shutdown();
    }
}
