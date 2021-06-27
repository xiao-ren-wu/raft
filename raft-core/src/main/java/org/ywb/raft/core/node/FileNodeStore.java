package org.ywb.raft.core.node;

import org.ywb.raft.core.exceptions.NodeStoreException;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.Files;
import org.ywb.raft.core.utils.RandomAccessFileAdapter;
import org.ywb.raft.core.utils.SeekableFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:55 下午 星期三
 * @since 1.0.0
 * 基于文件的形式存储
 * 节点持久化格式
 * +-------------+------------+---------+
 * | currentTerm | voteForLen | voteFor |
 * +-------------+------------+---------+
 * |    4字节     |    4字节   |  n字节   |
 * +-------------+------------+---------+
 */
public class FileNodeStore implements NodeStore {

    public static final String FILE_NAME = "node.bin";

    private static final long OFFSET_TERM = 0;

    private static final long OFFSET_VOTED_FOR = 4;

    private final SeekableFile seekableFile;

    private int term;

    private NodeId votedFor;

    public FileNodeStore() {
        String path = Objects.requireNonNull(FileNodeStore.class.getClassLoader().getResource("")).getPath();
        File file = new File(String.join("/", path, FILE_NAME));
        try {
            Files.createFileIfNotExist(file);
            this.seekableFile = new RandomAccessFileAdapter(file);
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    public FileNodeStore(File file) {
        try {
            Files.createFileIfNotExist(file);
            this.seekableFile = new RandomAccessFileAdapter(file);
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    public FileNodeStore(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
        try {
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        try {
            seekableFile.seek(OFFSET_TERM);
            seekableFile.writeInt(term);
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.term = term;
    }

    @Override
    public NodeId getVotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(NodeId votedFor) {
        try {
            seekableFile.seek(OFFSET_VOTED_FOR);
            if (votedFor == null) {
                seekableFile.writeInt(0);
                seekableFile.truncate(8L);
            } else {
                byte[] bytes = votedFor.getVal().getBytes(StandardCharsets.UTF_8);
                seekableFile.writeInt(bytes.length);
                seekableFile.write(bytes);
            }
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
        this.votedFor = votedFor;
    }

    @Override
    public void close() {
        try {
            seekableFile.close();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    private void initializeOrLoad() throws IOException {
        if (seekableFile.size() == 0) {
            /*
             * 初始化
             * (term,4) + (votedFor length, 4) = 8
             */
            seekableFile.truncate(8L);
            seekableFile.seek(0);
            // term
            seekableFile.writeInt(0);
            // voted for len
            seekableFile.writeInt(0);
            this.term = 0;
            this.votedFor = null;
        } else {
            // 加载
            // 读取term
            term = seekableFile.readInt();
            // 读取votedFor
            int len = seekableFile.readInt();
            if (len > 0) {
                byte[] bytes = new byte[len];
                seekableFile.read(bytes);
                votedFor = new NodeId(new String(bytes));
            }
        }
    }
}
