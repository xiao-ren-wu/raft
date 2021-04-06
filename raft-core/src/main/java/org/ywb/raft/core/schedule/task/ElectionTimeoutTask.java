package org.ywb.raft.core.schedule.task;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author yuwenbo1
 * @date 2021/4/6 10:50 下午 星期二
 * @since 1.0.0
 * 选举超时定时器
 */
public class ElectionTimeoutTask {

    private final ScheduledFuture<?> scheduledFuture;

    public ElectionTimeoutTask(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    /**
     * 取消选举超时
     */
    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        if (this.scheduledFuture.isCancelled()) {
            return "ElectionTimeout(state=cancelled)";
        } else if (this.scheduledFuture.isDone()) {
            return "ElectionTimeout(state=done)";
        }
        return "ElectionTimeout{delay=" + scheduledFuture.getDelay(TimeUnit.MILLISECONDS) + "}";
    }
}
