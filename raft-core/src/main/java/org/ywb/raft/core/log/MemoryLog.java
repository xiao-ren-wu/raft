package org.ywb.raft.core.log;

import com.google.common.eventbus.EventBus;
import org.ywb.raft.core.log.sequence.MemoryEntrySequence;

/**
 * @author yuwenbo1
 * @date 2021/4/27 7:37 上午 星期二
 * @since 1.0.0
 */
public class MemoryLog extends AbstractLog {

    public MemoryLog(MemoryEntrySequence memoryEntrySequence, EventBus eventBus) {
        super(eventBus);
        this.entrySequence = memoryEntrySequence;
    }
}
