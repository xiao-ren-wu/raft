package org.ywb.raft.core.log;

import org.ywb.raft.core.exceptions.EmptySequenceException;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.log.entry.EntrySequence;
import org.ywb.raft.core.utils.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/22 10:26 下午 星期四
 * @since 1.0.0
 *                            lastLogIndex
 *                              |
 * +-------+-------+-------+-------+- - - -+
 * |   E   |   E   |   E   |   E   |   E   |
 * +-------+-------+-------+-------+- - - -+
 *     |                               |
 * logIndexOffset                 nextLogIndex
 */
public abstract class AbstractEntrySequence implements EntrySequence {

    protected int logIndexOffset;

    protected   int nextLogIndex;

    public AbstractEntrySequence(int logIndexOffset) {
        this.logIndexOffset = logIndexOffset;
        this.nextLogIndex = logIndexOffset;
    }


    @Override
    public boolean isEmpty() {
        return nextLogIndex == logIndexOffset;
    }

    @Override
    public int getFirstLogIndex() {
        Assert.isFalse(isEmpty(), EmptySequenceException::new);
        return doGetFirstLogIndex();
    }


    @Override
    public int getLastLogIndex() {
        Assert.isFalse(isEmpty(), EmptySequenceException::new);
        return doGetLastLogIndex();
    }

    @Override
    public int getNextLogIndex() {
        return nextLogIndex;
    }

    @Override
    public List<Entry> subList(int fromIndex) {
        if (isEmpty() || fromIndex > doGetLastLogIndex()) {
            return Collections.emptyList();
        }
        return subList(Math.max(fromIndex, doGetFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) {
        Assert.isFalse(isEmpty(), EmptySequenceException::new);
        if (fromIndex < doGetFirstLogIndex() || toIndex > doGetLastLogIndex() + 1 || fromIndex > toIndex) {
            throw new IllegalArgumentException("illegal from index " + fromIndex + "or to index " + toIndex);
        }
        return doSubList(fromIndex, toIndex);
    }


    @Override
    public boolean isEntryPresent(int index) {
        return !isEmpty() && index >= doGetFirstLogIndex() && index <= doGetLastLogIndex();
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        Entry entry = doGetEntry(index);
        return entry.getMeta();
    }

    @Override
    public Entry getEntry(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        return doGetEntry(index);
    }

    @Override
    public Entry getLastEntry() {
        int lastLogIndex = getLastLogIndex();
        return getEntry(lastLogIndex);
    }

    @Override
    public void append(List<Entry> entries) {
        entries.forEach(this::append);
    }

    @Override
    public void removeAfter(int index) {
        if (isEmpty() || index >= doGetLastLogIndex()) {
            return;
        }
        doRemoveAfter(index);
    }

    @Override
    public void append(Entry entry) {
        if (entry.getIndex() != nextLogIndex) {
            throw new IllegalArgumentException("entry index must be " + nextLogIndex);
        }
        doAppend(entry);
        nextLogIndex++;
    }


    /**
     * 获取指定索引的日志条目
     *
     * @param index 日志索引
     * @return entry
     */
    protected abstract Entry doGetEntry(int index);

    /**
     * 截取list
     *
     * @param fromIndex begin
     * @param toIndex   end
     * @return sub list
     */
    protected abstract List<Entry> doSubList(int fromIndex, int toIndex);

    /**
     * 追加日志
     *
     * @param entry entry
     */
    protected abstract void doAppend(Entry entry);

    /**
     * 移除index后日志
     *
     * @param index begin remove index
     */
    protected abstract void doRemoveAfter(int index);


    protected int doGetFirstLogIndex() {
        return logIndexOffset;
    }

    protected int doGetLastLogIndex() {
        return nextLogIndex - 1;
    }
}
