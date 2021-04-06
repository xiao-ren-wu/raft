package org.ywb.raft.core.schedule;

import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.schedule.task.LogReplicationTask;

/**
 * @author yuwenbo1
 * @date 2021/4/6 10:55 下午 星期二
 * @since 1.0.0
 */
public interface Scheduler {
    /**
     * 创建日志复制定时器
     *
     * @param task 任务
     * @return {@link LogReplicationTask}
     */
    LogReplicationTask scheduleLogReplicationTask(Runnable task);

    /**
     * 创建选举超时定时器
     *
     * @param task 任务
     * @return {@link ElectionTimeoutTask}
     */
    ElectionTimeoutTask scheduleElectionTimeoutTask(Runnable task);

    /**
     * 关闭定时器
     *
     * @throws InterruptedException e
     */
    void stop() throws InterruptedException;
}
