package org.ywb.raft.core.schedule;

import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.schedule.task.LogReplicationTask;
import org.ywb.raft.core.utils.Assert;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author yuwenbo1
 * @date 2021/4/6 10:58 下午 星期二
 * @since 1.0.0
 */
public class DefaultScheduler implements Scheduler {

    /**
     * 最小选举超时时间
     */
    private final int minElectionTimeout;

    /**
     * 最大选举超时时间
     */
    private final int maxElectionTimeout;

    /**
     * 初次日志复制延迟时间
     */
    private final int logReplicationDelay;

    /**
     * 日志复制间隔
     */
    private final int logReplicationInterval;

    /**
     * 随机数生成器
     */
    private final Random electionTimeoutRandom;

    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultScheduler(int minElectionTimeout, int maxElectionTimeout,
                            int logReplicationDelay, int logReplicationInterval) {
        Assert.isFalse(minElectionTimeout <= 0 || maxElectionTimeout <= 0 || minElectionTimeout > maxElectionTimeout,
                () -> new IllegalArgumentException("election timeout should not be 0 or min > max")
        );
        Assert.isFalse(logReplicationInterval < 0 || logReplicationDelay <= 0,
                () -> new IllegalArgumentException("log Replication Interval Or Delay must be > 0")
        );
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInterval = logReplicationInterval;
        this.electionTimeoutRandom = new Random();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "default-scheduler"));
    }

    @Override
    public LogReplicationTask scheduleLogReplicationTask(Runnable task) {
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(task, logReplicationDelay, logReplicationInterval, TimeUnit.MILLISECONDS);
        return new LogReplicationTask(scheduledFuture);
    }

    @Override
    public ElectionTimeoutTask scheduleElectionTimeoutTask(Runnable task) {
        // 随机选举超时时间
        int timeout = electionTimeoutRandom.nextInt(maxElectionTimeout - minElectionTimeout) + minElectionTimeout;
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.schedule(task, timeout, TimeUnit.MILLISECONDS);
        return new ElectionTimeoutTask(scheduledFuture);
    }

    @Override
    public void stop() throws InterruptedException {
        this.scheduledExecutorService.shutdown();
    }
}
