package org.ywb.raft.core.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.support.SingleThreadTaskExecutor;
import org.ywb.raft.core.support.TaskExecutor;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:44 下午 星期一
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractSingleThreadStateMachine implements StateMachine {

    private volatile int lastApplied = 0;

    private final TaskExecutor taskExecutor;

    public AbstractSingleThreadStateMachine() {
        this.taskExecutor = new SingleThreadTaskExecutor("state-machine");
    }

    public void applyLog(StateMachineContext context, int index, byte[] commandBytes, int firstLogIndex) {
        taskExecutor.submit(() -> doApplyLog(context, index, commandBytes, firstLogIndex));
    }

    private void doApplyLog(StateMachineContext context, int index, byte[] commandBytes, int firstLogIndex) {
        if (index <= lastApplied) {
            return;
        }
        log.debug("apply log {}", index);
        applyCommand(commandBytes);
        lastApplied = index;
    }

    @Override
    public int getLastApplied() {
        return lastApplied;
    }

    @Override
    public void shutdown() {
        try {
            taskExecutor.shutdown();
        } catch (InterruptedException e) {
            throw new StateMachineException(e);
        }
    }

    /**
     * 追加命令
     *
     * @param commandBytes command bytes
     */
    protected abstract void applyCommand(byte[] commandBytes);
}
