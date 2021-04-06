package org.ywb.raft.core.schedule.task;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author yuwenbo1
 * @date 2021/4/6 10:46 下午 星期二
 * @since 1.0.0
 * 日志复制定时器
 */
public class LogReplicationTask {

    private final ScheduledFuture<?> scheduledFuture;

    public LogReplicationTask(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    /**
     * 取消日志复制定时器
     * 如果正在执行，不取消
     */
    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        return "LogReplicationTask{delay=" +
                scheduledFuture.getDelay(TimeUnit.MILLISECONDS) +
                '}';
    }
}
