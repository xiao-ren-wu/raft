package org.ywb.raft.core.statemachine;

import org.ywb.raft.core.log.entry.Entry;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:11 下午 星期一
 * @since 1.0.0
 */
public interface StateMachine {

    /**
     * 获取上一个lastApplied
     *
     * @return termId
     */
    int getLastApplied();

    /**
     * 应用日志
     *
     * @param context       context
     * @param index         index
     * @param commandBytes  commandBytes
     * @param firstLogIndex firstLogIndex
     */
//    void applyLog(StateMachineContext context, int index, byte[] commandBytes, int firstLogIndex);

    /**
     * 关闭状态机
     */
    void shutdown();
}
