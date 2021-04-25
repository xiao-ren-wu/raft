package org.ywb.raft.core.log.entry;

import org.ywb.raft.core.utils.RandomAccessFileAdapter;
import org.ywb.raft.core.utils.SeekableFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yuwenbo1
 * @date 2021/4/25 10:19 下午 星期日
 * @since 1.0.0
 */
public class EntryIndexFile implements Iterable<EntryIndexItem> {

    /**
     * 单条日志索引最大偏移量
     */
    private static final long OFFSET_MAX_ENTRY_INDEX = Integer.BYTES;

    /**
     * 单条日志条目元信息长度
     */
    private static final int LENGTH_ENTRY_INDEX_ITEM = 16;

    private final SeekableFile seekableFile;

    /**
     * 最小日志索引
     */
    private int minEntryIndex;

    /**
     * 最大日志索引
     */
    private int maxEntryIndex;

    private Map<Integer,EntryIndexItem> entryIndexItemMap = new HashMap<>();

    public EntryIndexFile(File file) throws FileNotFoundException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntryIndexFile(SeekableFile seekableFile){
        this.seekableFile = seekableFile;
        load(seekableFile);
    }

    private void load(SeekableFile seekableFile) {

    }

    @Override
    public Iterator<EntryIndexItem> iterator() {
        return null;
    }
}
