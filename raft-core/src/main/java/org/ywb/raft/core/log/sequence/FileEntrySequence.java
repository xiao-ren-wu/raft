package org.ywb.raft.core.log.sequence;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.exceptions.LogException;
import org.ywb.raft.core.log.dir.LogDir;
import org.ywb.raft.core.log.entry.EntriesFile;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.log.index.EntryIndexFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/26 8:12 上午 星期一
 * @since 1.0.0
 */
@Slf4j
public class FileEntrySequence extends AbstractEntrySequence {

    private final EntriesFile entriesFile;

    private final EntryIndexFile entryIndexFile;

    private final LinkedList<Entry> pendingEntries = new LinkedList<>();

    /**
     * raft算法中定义最初始commitIndex为0，和日志是否持久化无关
     */
    private int commitIndex = 0;

    public FileEntrySequence(LogDir logDir, int logIndexOffset) {
        super(logIndexOffset);
        try {
            this.entriesFile = new EntriesFile(logDir.getEntriesFile());
            this.entryIndexFile = new EntryIndexFile(logDir.getEntryOffsetIndexFile());
            initialize();
        } catch (IOException e) {
            throw new LogException("failed to open entries file or entry index file", e);
        }
    }

    public FileEntrySequence(int logIndexOffset, EntriesFile entriesFile, EntryIndexFile entryIndexFile) {
        super(logIndexOffset);
        this.entriesFile = entriesFile;
        this.entryIndexFile = entryIndexFile;
        initialize();
    }

    @Override
    protected Entry doGetEntry(int index) {
        if (!pendingEntries.isEmpty()) {
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex) {
                return pendingEntries.get(index - firstPendingEntryIndex);
            }
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(index);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        // 结果分为来自文件和来自缓冲两部分
        ArrayList<Entry> result = new ArrayList<>();
        // 从文件中获取条目日志
        if (!entryIndexFile.isEmpty() && fromIndex <= entryIndexFile.getMaxEntryIndex()) {
            int maxIndex = Math.min(entryIndexFile.getMaxEntryIndex() + 1, toIndex);
            for (int i = fromIndex; i < maxIndex; i++) {
                result.add(getEntryInFile(i));
            }
        }
        // 从日志缓冲中获取日志条目
        if (!pendingEntries.isEmpty() && toIndex > pendingEntries.getFirst().getIndex()) {
            Iterator<Entry> iterator = pendingEntries.iterator();
            Entry entry;
            int index;
            while (iterator.hasNext()) {
                entry = iterator.next();
                index = entry.getIndex();
                if (index > toIndex) {
                    break;
                }
                if (index >= fromIndex) {
                    result.add(entry);
                }
            }
        }
        return result;
    }


    @Override
    protected void doAppend(Entry entry) {
        pendingEntries.add(entry);
    }

    @Override
    protected void doRemoveAfter(int index) {
        // 只需要移除索引之后的日志条目
        if (!pendingEntries.isEmpty() && index >= pendingEntries.getFirst().getIndex() - 1) {
            for (int i = index + 1; i <= doGetLastLogIndex(); i++) {
                pendingEntries.removeLast();
            }
            nextLogIndex = index + 1;
            return;
        }
        try {
            if (index >= doGetFirstLogIndex()) {
                // 索引比日志缓冲的第一条日志小
                pendingEntries.clear();
                entriesFile.truncate(entryIndexFile.getOffset(index + 1));
                entryIndexFile.removeAfter(index);
                nextLogIndex = index + 1;
                commitIndex = index;
            } else {
                // 如果索引比第一条日志索引都小，则清除所有数据
                pendingEntries.clear();
                entriesFile.clear();
                nextLogIndex = logIndexOffset;
                commitIndex = logIndexOffset - 1;
            }
        } catch (IOException e) {
            throw new LogException(e);
        }
    }

    @Override
    public void commit(int index) {
        if (index < commitIndex) {
            throw new IllegalArgumentException("commit index < " + commitIndex);
        }
        if (index == commitIndex) {
            return;
        }
        // 如果commitIndex在文件内，则只更新commitIndex
        if (!entryIndexFile.isEmpty() && index <= entryIndexFile.getMaxEntryIndex()) {
            commitIndex = index;
            return;
        }
        // 检查commitIndex是否在日志缓冲区间内
        if (pendingEntries.isEmpty() ||
                pendingEntries.getFirst().getIndex() > index ||
                pendingEntries.getLast().getIndex() < index
        ) {
            throw new IllegalArgumentException("no entry to commit or commit index exceed.");
        }
        long offset;
        Entry entry = null;
        try {
            for (int i = pendingEntries.getFirst().getIndex(); i <= index; i++) {
                entry = pendingEntries.removeFirst();
                offset = entriesFile.appendEntry(entry);
                entryIndexFile.appendEntryIndex(i, offset, entry.getKind(), entry.getTerm());
                commitIndex = i;
            }
        } catch (IOException e) {
            throw new LogException("failed to commit entry " + entry, e);
        }
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() throws IOException {
        entriesFile.close();
        pendingEntries.clear();
    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        if (!pendingEntries.isEmpty()) {
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex) {
                return pendingEntries.get(index - firstPendingEntryIndex).getMeta();
            }
        }
        return entryIndexFile.get(index).toEntryMeta();
    }

    @Override
    public Entry getLastEntry() {
        if (isEmpty()) {
            return null;
        }
        if (!pendingEntries.isEmpty()) {
            return pendingEntries.getLast();
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(entryIndexFile.getMaxEntryIndex());
    }


    private void initialize() {
        if (entryIndexFile.isEmpty()) {
            commitIndex = logIndexOffset - 1;
            return;
        }
        // 使用日志索引文件的minEntryIndex作为logIndexOffset
        logIndexOffset = entryIndexFile.getMinEntryIndex();
        // 使用日志索引文件的maxIndexFie#getMaxEntryIndex加1作为nextLongOffset
        nextLogIndex = entryIndexFile.getMaxEntryIndex() + 1;
        commitIndex = entryIndexFile.getMaxEntryIndex();
    }

    private Entry getEntryInFile(int index) {
        long offset = entryIndexFile.getOffset(index);
        try {
            return entriesFile.loadEntry(offset);
        } catch (IOException e) {
            throw new LogException("failed to load entry " + index, e);
        }
    }
}
