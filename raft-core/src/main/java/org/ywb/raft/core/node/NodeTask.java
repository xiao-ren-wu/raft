package org.ywb.raft.core.node;

import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;

/**
 * @author yuwenbo1
 * @date 2021/4/11 9:50 下午 星期日
 * @since 1.0.0
 */
public interface NodeTask {
    /**
     * 设置选举超时定时器
     * 1. 选举超时需要变更节点角色
     * 2. 发送RequestVote消息给其他节点
     *
     * @return 选举超时任务句柄
     */
    ElectionTimeoutTask scheduleElectionTimeout();
}
