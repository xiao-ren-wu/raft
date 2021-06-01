package org.ywb.raft.core.statemachine;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:51 下午 星期一
 * @since 1.0.0
 */
public class StateMachineException extends RuntimeException {
    public StateMachineException(InterruptedException e) {
        super(e);
    }
}
