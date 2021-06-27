package org.ywb.raft.core.log;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.log.entry.GeneralEntry;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.log.sequence.EntrySequence;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Assert;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/4/26 10:11 下午 星期一
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractLog implements Log {

    private EventBus eventBus;

    protected EntrySequence entrySequence;

    public AbstractLog(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public EntryMeta getLastEntryMeta() {
        if (entrySequence.isEmpty()) {
            return new EntryMeta(Entry.KIND_NO_OP, 0, 0);
        }
        return entrySequence.getLastEntry().getMeta();
    }

    @Override
    public AppendEntriesRpc createAppendEntriesRpc(int term, NodeId selfId, int nextIndex, int maxEntries) {
        int nextLogIndex = entrySequence.getNextLogIndex();
        if (nextIndex > nextLogIndex) {
            throw new IllegalArgumentException("illegal next index " + nextIndex);
        }
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(term);
        rpc.setLeaderId(selfId);
        rpc.setLeaderCommit(getCommitIndex());
        Entry entry = entrySequence.getEntry(nextIndex - 1);
        if (Objects.nonNull(entry)) {
            rpc.setPrevLogIndex(entry.getIndex());
            rpc.setPrevLogTerm(entry.getTerm());
        }
        // 设置entries
        if (!entrySequence.isEmpty()) {
            int maxIndex = (maxEntries == ALL_ENTRIES ? nextLogIndex : Math.min(nextLogIndex, nextIndex + maxEntries));
            rpc.setEntries(entrySequence.subList(nextIndex, maxIndex));
        }
        return rpc;
    }

    @Override
    public int getNextIndex() {
        return entrySequence.getNextLogIndex();
    }

    @Override
    public int getCommitIndex() {
        return entrySequence.getCommitIndex();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isNewThan(int lastLogIndex, int lastLogTerm) {
        EntryMeta lastEntryMeta = getLastEntryMeta();
        log.debug("last entry ({},{}),candidate,({}{})", lastEntryMeta.getIndex(), lastEntryMeta.getTerm(), lastLogIndex, lastLogTerm);
        return lastEntryMeta.getTerm() > lastLogTerm || lastEntryMeta.getIndex() > lastLogIndex;
    }

    @Override
    public NoOpEntry appendEntry(int term) {
        NoOpEntry noOpEntry = new NoOpEntry(entrySequence.getNextLogIndex(), term);
        entrySequence.append(noOpEntry);
        return noOpEntry;
    }

    @Override
    public GeneralEntry appendEntry(int term, byte[] command) {
        GeneralEntry entry = new GeneralEntry(entrySequence.getNextLogIndex(), term, command);
        entrySequence.append(entry);
        return entry;
    }

    @Override
    public boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> leaderEntries) {
        // 检查前一条日志是否匹配
        if (!checkIfPreviousLogMatches(prevLogIndex, prevLogTerm)) {
            return false;
        }
        // Leader节点传递过来的日志条目为空
        if (leaderEntries.isEmpty()) {
            return true;
        }
        // 移除冲突的日志条目并返回接下来要追加的日志条目（如果日志还有的话）
        EntrySequenceView newEntries = removeUnmatchedLog(new EntrySequenceView(leaderEntries));
        // 仅追加日志
        appendEntriesFromLeader(newEntries);
        return true;
    }

    private void appendEntriesFromLeader(EntrySequenceView leaderSeqEntries) {
        if (leaderSeqEntries.isEmpty()) {
            return;
        }
        log.debug("append entries from leader from {} to {}", leaderSeqEntries.getFirstLogIndex(), leaderSeqEntries.getLastLogIndex());
        for (Entry leaderEntry : leaderSeqEntries) {
            entrySequence.append(leaderEntry);
        }
    }

    private EntrySequenceView removeUnmatchedLog(EntrySequenceView leaderEntries) {
        // 从leader节点过来的entries不能为空
        Assert.nonNull(leaderEntries);
        int firstUnmatched = findFirstUnmatchedLog(leaderEntries);
        // 没有不匹配的日志
        if (firstUnmatched < 0) {
            return new EntrySequenceView(Collections.emptyList());
        }
        // 移除不匹配的日志索引开始的所有日志
        removeEntriesAfter(firstUnmatched - 1);
        // 返回之后追加的日志条目
        return leaderEntries.subView(firstUnmatched);
    }

    @Override
    public void advanceCommitIndex(int newCommitIndex, int currentTerm) {
        if (!validateNewCommitIndex(newCommitIndex, currentTerm)) {
            return;
        }
        log.debug("advance commit index from {} to {}", newCommitIndex, currentTerm);
        entrySequence.commit(newCommitIndex);
        // todo advanceApplyIndex();
    }

    private boolean validateNewCommitIndex(int newCommitIndex, int currentTerm) {
        // 小于当前的commitIndex
        if (newCommitIndex <= entrySequence.getCommitIndex()) {
            return false;
        }
        EntryMeta meta = entrySequence.getEntryMeta(newCommitIndex);
        if (Objects.isNull(meta)) {
            log.debug("log of new commit index {} not found", newCommitIndex);
            return false;
        }
        // 日志条目的term必须是当前的term，才能推进commitIndex
        if (meta.getTerm() != currentTerm) {
            log.debug("log term of new commit index != current term ({}!={})", meta.getTerm(), currentTerm);
            return false;
        }
        return true;
    }

    private void removeEntriesAfter(int index) {
        if (entrySequence.isEmpty() || index >= entrySequence.getLastLogIndex()) {
            return;
        }
        // 此处已经移除了已经应用的日志，需要重头开始重新构建状态机
        log.debug("remove entries after {}", index);
        entrySequence.removeAfter(index);
    }

    private int findFirstUnmatchedLog(EntrySequenceView leaderEntries) {
        int logIndex;
        EntryMeta followerEntryMeta;
        for (Entry leaderEntry : leaderEntries) {
            logIndex = leaderEntry.getIndex();
            followerEntryMeta = entrySequence.getEntryMeta(logIndex);
            if (followerEntryMeta == null || followerEntryMeta.getTerm() != leaderEntry.getTerm()) {
                return logIndex;
            }
        }
        return -1;
    }

    private boolean checkIfPreviousLogMatches(int prevLogIndex, int prevLogTerm) {
        EntryMeta prevEntryMeta = entrySequence.getEntryMeta(prevLogIndex);
        if (Objects.isNull(prevEntryMeta)) {
            log.debug("previous log {} not found", prevLogIndex);
            return false;
        }
        int term = prevEntryMeta.getTerm();
        if (term != prevLogTerm) {
            log.warn("different term of previous log,local {},remote {}", term, prevEntryMeta);
            return false;
        }
        return true;
    }

    private static class EntrySequenceView implements Iterable<Entry> {

        private final List<Entry> entries;

        private int firstLogIndex = -1;

        private int lastLogIndex = -1;

        public EntrySequenceView(List<Entry> entries) {
            this.entries = entries;
            if (!entries.isEmpty()) {
                firstLogIndex = entries.get(0).getIndex();
                lastLogIndex = entries.get(entries.size() - 1).getIndex();
            }
        }

        // 获取指定位置的日志条目
        Entry get(int index) {
            if (entries.isEmpty() || index < firstLogIndex || index > lastLogIndex) {
                return null;
            }
            return entries.get(index - firstLogIndex);
        }

        boolean isEmpty() {
            return entries.isEmpty();
        }

        int getFirstLogIndex() {
            return firstLogIndex;
        }

        int getLastLogIndex() {
            return lastLogIndex;
        }

        EntrySequenceView subView(int fromIndex) {
            if (entries.isEmpty() || fromIndex > lastLogIndex) {
                return new EntrySequenceView(Collections.emptyList());
            }
            return new EntrySequenceView(entries.subList(fromIndex - firstLogIndex, entries.size()));
        }

        @Override
        @Nonnull
        public Iterator<Entry> iterator() {
            return entries.iterator();
        }
    }

}
