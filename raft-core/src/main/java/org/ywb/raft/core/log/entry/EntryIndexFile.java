package org.ywb.raft.core.log.entry;

import lombok.Getter;
import org.ywb.raft.core.utils.RandomAccessFileAdapter;
import org.ywb.raft.core.utils.SeekableFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yuwenbo1
 * @date 2021/4/25 10:19 下午 星期日
 * @since 1.0.0
 * 日志索引文件结构
 */
public class EntryIndexFile implements Iterable<EntryIndexItem> {
    /*
       +--------------------+---------------------+
       | minEntryIndex(int4)| maxEntryIndex(int4) |
       +--------------------+---------------------+-------------+------------+
       |               offset(long)               |  kind(int4) | term(int4) |
       +------------------------------------------+-------------+------------+
       |               offset(long)               |  kind(int4) | term(int4) |
       +------------------------------------------+-------------+------------+
       |               offset(long)               |  kind(int4) | term(int4) |
       +------------------------------------------+-------------+------------+
     */
    /**
     * 单条日志索引最大偏移量
     */
    private static final long OFFSET_MAX_ENTRY_INDEX = Integer.BYTES;

    /**
     * 单条日志条目元信息长度
     */
    private static final int LENGTH_ENTRY_INDEX_ITEM = 16;

    /**
     * 日志条目数
     */
    private int entryIndexCount;

    private final SeekableFile seekableFile;

    /**
     * 最小日志索引
     */
    @Getter
    private int minEntryIndex;

    /**
     * 最大日志索引
     */
    @Getter
    private int maxEntryIndex;

    private Map<Integer,EntryIndexItem> entryIndexItemMap = new HashMap<>();

    public EntryIndexFile(File file) throws IOException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntryIndexFile(SeekableFile seekableFile) throws IOException {
        this.seekableFile = seekableFile;
        load(seekableFile);
    }

    /**
     * 加载所有日志的元信息
     *
     * @param seekableFile file index
     */
    private void load(SeekableFile seekableFile) throws IOException {
        if (seekableFile.size() == 0L) {
            entryIndexCount = 0;
        }
        minEntryIndex = seekableFile.readInt();
        maxEntryIndex = seekableFile.readInt();
        updateEntryIndexCount();
        // 逐条加载
        long offset;
        int kind;
        int term;
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            offset = seekableFile.readLong();
            kind = seekableFile.readInt();
            term = seekableFile.readInt();
            entryIndexItemMap.put(i, new EntryIndexItem(i, offset, kind, term));
        }
    }

    @Override
    public Iterator<EntryIndexItem> iterator() {
        if (entryIndexItemMap.isEmpty()) {
            return Collections.emptyIterator();
        }
        return new EntryIndexIterator(entryIndexCount, minEntryIndex);
    }

    /**
     * 追加日志，只能顺序增加
     */
    public void appendEntryIndex(int index, long offset, int kind, int term) throws IOException {
        if (seekableFile.size() == 0L) {
            seekableFile.writeInt(index);
            minEntryIndex = index;
        } else {
            if (index != maxEntryIndex + 1) {
                throw new IllegalArgumentException("index must be " + (maxEntryIndex + 1) + ",but was" + index);
            }
            seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);

            seekableFile.writeInt(index);
            this.maxEntryIndex = index;
            updateEntryIndexCount();

            seekableFile.seek(getOffsetOfEntryIndexItem(index));
            seekableFile.writeLong(offset);
            seekableFile.writeInt(kind);
            seekableFile.writeInt(term);

            entryIndexItemMap.put(index, new EntryIndexItem(index, offset, kind, term));
        }
    }

    private long getOffsetOfEntryIndexItem(int index) {
        return (long) (index - minEntryIndex) * LENGTH_ENTRY_INDEX_ITEM + Integer.BYTES * 2;
    }


    /**
     * 更新日志条目信息
     */
    private void updateEntryIndexCount() {
        entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }

    public void clear() throws IOException {
        seekableFile.truncate(0L);
        entryIndexCount = 0;
        entryIndexItemMap.clear();
    }

    /**
     * 移除指定索引之后的日志元信息
     *
     * @param newMaxEntryIndex 边界
     * @throws IOException E
     */
    public void removeAfter(int newMaxEntryIndex) throws IOException {
        if (entryIndexItemMap.isEmpty() || newMaxEntryIndex >= maxEntryIndex) {
            return;
        }
        if (newMaxEntryIndex < minEntryIndex) {
            clear();
            return;
        }
        seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        seekableFile.writeInt(newMaxEntryIndex);
        seekableFile.truncate(getOffsetOfEntryIndexItem(newMaxEntryIndex + 1));
        // 移除缓存中的元信息
        for (int i = newMaxEntryIndex; i < maxEntryIndex; i++) {
            entryIndexItemMap.remove(i);
        }
        maxEntryIndex = newMaxEntryIndex;
        entryIndexCount = newMaxEntryIndex - minEntryIndex + 1;
    }

    public boolean isEmpty() {
        return 0 == entryIndexCount;
    }

    public EntryIndexItem get(int index) {
        return entryIndexItemMap.get(index);
    }

    public long getOffset(int index) {
        EntryIndexItem entryIndexItem = entryIndexItemMap.get(index);
        return entryIndexItem.getOffset();
    }

    private class EntryIndexIterator implements Iterator<EntryIndexItem> {
        /**
         * 条目总数
         */
        private final int entryIndexCount;
        /**
         * 当前索引
         */
        private int currentEntryIndex;

        public EntryIndexIterator(int entryIndexCount, int currentEntryIndex) {
            this.entryIndexCount = entryIndexCount;
            this.currentEntryIndex = currentEntryIndex;
        }

        @Override
        public boolean hasNext() {
            checkModification();
            return currentEntryIndex <= maxEntryIndex;
        }

        @Override
        public EntryIndexItem next() {
            checkModification();
            return entryIndexItemMap.get(currentEntryIndex++);
        }

        private void checkModification() {
            if (this.entryIndexCount != EntryIndexFile.this.entryIndexCount) {
                throw new IllegalArgumentException("entry index count changed");
            }
        }

    }


}
