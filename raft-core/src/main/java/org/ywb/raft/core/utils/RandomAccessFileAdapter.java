package org.ywb.raft.core.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

/**
 * @author yuwenbo1
 * @date 2021/4/11 5:03 下午 星期日
 * @since 1.0.0
 */
public class RandomAccessFileAdapter implements SeekableFile {

    private final RandomAccessFile randomAccessFile;

    public RandomAccessFileAdapter(File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "rws");
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
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
    public void truncate(long size) throws IOException {
        randomAccessFile.setLength(size);
    }

    @Override
    public int readInt() throws IOException {
        return randomAccessFile.readInt();
    }

    @Override
    public void read(byte[] bytes) throws IOException {
        randomAccessFile.read(bytes);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        randomAccessFile.write(bytes);
    }

    @Override
    public long readLong() throws IOException {
        return randomAccessFile.readLong();
    }

    @Override
    public void writeLong(long l) throws IOException {
        randomAccessFile.writeLong(l);
    }

    @Override
    public InputStream inputStream(long start) throws IOException {
        throw new NotImplementedException();
    }
}
