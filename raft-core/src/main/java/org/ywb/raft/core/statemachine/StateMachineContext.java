package org.ywb.raft.core.statemachine;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:57 下午 星期一
 * @since 1.0.0
 */
public interface StateMachineContext {
    void generateSnapshot(int lastIncludedIndex);
}
