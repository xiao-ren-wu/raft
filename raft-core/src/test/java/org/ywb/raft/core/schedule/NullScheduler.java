package org.ywb.raft.core.schedule;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.schedule.task.LogReplicationTask;

/**
 * @author yuwenbo1
 * @date 2021/4/11 11:44 上午 星期日
 * @since 1.0.0
 */
@Slf4j
public class NullScheduler implements Scheduler {

    @Override
    public LogReplicationTask scheduleLogReplicationTask(Runnable task) {
        log.debug("schedule log replication task");
        return LogReplicationTask.NONE;
    }

    @Override
    public ElectionTimeoutTask scheduleElectionTimeoutTask(Runnable task) {
        log.debug("schedule election timeout task");
        return ElectionTimeoutTask.NONE;
    }

    @Override
    public void stop() throws InterruptedException {

    }
}
