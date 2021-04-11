package org.ywb.raft.core.schedule;

import java.util.concurrent.*;

/**
 * @author yuwenbo1
 * @date 2021/4/11 11:49 上午 星期日
 * @since 1.0.0
 */
public class NullScheduleFuture implements ScheduledFuture<Object> {

    @Override
    public long getDelay(TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
