package org.ywb.raft.core.log;

import org.ywb.raft.core.log.sequence.MemoryEntrySequence;

/**
 * @author yuwenbo1
 * @date 2021/4/27 7:37 上午 星期二
 * @since 1.0.0
 */
public class MemoryLog extends AbstractLog {

    public MemoryLog() {
        this(new MemoryEntrySequence());
    }

    public MemoryLog(MemoryEntrySequence memoryEntrySequence) {
        this.entrySequence = memoryEntrySequence;
    }
}
