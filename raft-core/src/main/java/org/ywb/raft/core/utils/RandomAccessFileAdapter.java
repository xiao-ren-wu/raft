package org.ywb.raft.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author yuwenbo1
 * @date 2021/4/11 5:03 下午 星期日
 * @since 1.0.0
 */
public class RandomAccessFileAdapter implements SeekableFile {

    private RandomAccessFile randomAccessFile;

    public RandomAccessFileAdapter(File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "rws");
    }

    @Override
    public long size() throws IOException {
        return randomAccessFile.length();
    }

    @Override
    public void writeInt(int msg) throws IOException {
        randomAccessFile.writeInt(msg);
    }

    @Override
    public void seek(long index) throws IOException {
        randomAccessFile.seek(index);
    }

    @Override
    public void truncate(long size) {
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public void read(byte[] bytes) {

    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        randomAccessFile.write(bytes);
    }
}
