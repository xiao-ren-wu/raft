package org.ywb.raft.core.log.dir;

import org.ywb.raft.core.utils.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/4/27 7:54 上午 星期二
 * @since 1.0.0
 */
public class LogGeneration implements LogDir {

    private static final String ENTRY_FILE_NAME = "entries.bin";

    private static final String ENTRY_IDX_NAME = "entries.idx";

    private final File logDir;

    private File entryFile;

    private File entryIndexFile;

    public LogGeneration(File logDir) {
        this.logDir = logDir;
    }

    @Override
    public void initialize() {
        if (!logDir.exists()) {
            boolean mkdirs = logDir.mkdirs();
            Assert.isTrue(mkdirs, () -> new IllegalArgumentException("create dir failed " + logDir));
        }
    }

    @Override
    public boolean exists() {
        return logDir.exists();
    }

    @Override
    public File getEntriesFile() throws IOException {
        if (Objects.nonNull(entryFile)) {
            return entryFile;
        }
        String absolutePath = logDir.getAbsolutePath();
        String entryFilePath = String.format("%s/%s", absolutePath, ENTRY_FILE_NAME);
        File file = new File(entryFilePath);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            Assert.isTrue(newFile, () -> new IllegalArgumentException("create entries file failed" + entryFilePath));
            this.entryFile = file;
        }
        return file;
    }

    @Override
    public File getEntryOffsetIndexFile() throws IOException {
        if (Objects.nonNull(entryIndexFile)) {
            return entryIndexFile;
        }
        String absolutePath = logDir.getAbsolutePath();
        String entryIdxFilePath = String.format("%s/%s", absolutePath, ENTRY_IDX_NAME);
        File file = new File(entryIdxFilePath);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            Assert.isTrue(newFile, () -> new IllegalArgumentException("create entries idx file failed" + entryIdxFilePath));
            this.entryIndexFile = file;
        }
        return file;
    }

    @Override
    public File get() {
        return logDir;
    }

    @Override
    public boolean renameTo(LogDir logDir) {
        File file = logDir.get();
        return this.logDir.renameTo(file);
    }

    public int getLogIndexOffset() {
        return Integer.parseInt(logDir.getName().split("-")[1]);
    }
}
