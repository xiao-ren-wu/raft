package org.ywb.raft.core.log.entry;

import org.ywb.raft.core.utils.Assert;
import org.ywb.raft.core.utils.RandomAccessFileAdapter;
import org.ywb.raft.core.utils.SeekableFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author yuwenbo1
 * @date 2021/4/25 9:27 下午 星期日
 * @since 1.0.0
 */
public class EntriesFile {

    private final SeekableFile seekableFile;

    public EntriesFile(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
    }

    public EntriesFile(File file) throws FileNotFoundException {
        this(new RandomAccessFileAdapter(file));
    }

    /**
     * 追加日志条目
     *
     * @param entry entry
     * @return begin index
     * @throws IOException E
     */
    public long appendEntry(Entry entry) throws IOException {
        long offset = seekableFile.size();
        seekableFile.seek(offset);
        seekableFile.writeInt(entry.getKind());
        seekableFile.writeInt(entry.getIndex());
        seekableFile.writeInt(entry.getTerm());
        byte[] commandBytes = entry.getCommandBytes();
        seekableFile.writeInt(commandBytes.length);
        seekableFile.write(commandBytes);
        return offset;
    }

    /**
     * 从指定偏移量加载日志条目
     *
     * @param offset  指定偏移量
     * @return entry
     * @throws IOException E
     */
    public Entry loadEntry(long offset) throws IOException {
        Assert.isFalse(offset > seekableFile.size(), () -> new IllegalArgumentException("offset > size"));
        seekableFile.seek(offset);
        int kind = seekableFile.readInt();
        int index = seekableFile.readInt();
        int term = seekableFile.readInt();
        int length = seekableFile.readInt();
        byte[] commandBytes = new byte[length];
        seekableFile.read(commandBytes);
        return EntryFactory.create(kind, index, term, commandBytes);
    }


}
