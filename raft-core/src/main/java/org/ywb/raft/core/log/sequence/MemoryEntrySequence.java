package org.ywb.raft.core.log.sequence;

import lombok.ToString;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.sequence.AbstractEntrySequence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/25 8:46 下午 星期日
 * @since 1.0.0
 */
@ToString
public class MemoryEntrySequence extends AbstractEntrySequence {

    private final List<Entry> entries = new ArrayList<>();

    private int commitIndex = 0;

    public MemoryEntrySequence() {
        super(1);
    }

    public MemoryEntrySequence(int logIndexOffset) {
        super(logIndexOffset);
    }

    @Override
    protected Entry doGetEntry(int index) {
        return entries.get(index - logIndexOffset);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex - logIndexOffset, toIndex - logIndexOffset);
    }

    @Override
    protected void doAppend(Entry entry) {
        entries.add(entry);
    }

    @Override
    protected void doRemoveAfter(int index) {
        if (index < doGetFirstLogIndex()) {
            entries.clear();
            nextLogIndex = logIndexOffset;
        } else {
            entries.subList(index - logIndexOffset + 1, entries.size()).clear();
            nextLogIndex = index + 1;
        }
    }

    @Override
    public void commit(int index) {
        this.commitIndex = index;
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {

    }
}
