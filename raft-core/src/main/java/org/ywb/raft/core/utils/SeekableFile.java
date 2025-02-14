package org.ywb.raft.core.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yuwenbo1
 * @date 2021/4/11 5:01 下午 星期日
 * @since 1.0.0
 */
public interface SeekableFile {
    /**
     * 获取当前位置
     *
     * @return current position
     * @throws IOException e
     */
    long position() throws IOException;

    /**
     * 返回文件大小
     *
     * @return file size
     */
    long size() throws IOException;

    /**
     * 像文件中追加一个int
     *
     * @param msg int val
     */
    void writeInt(int msg) throws IOException;

    /**
     * 跳跃到指定位置
     *
     * @param index index   i
     */
    void seek(long index) throws IOException;

    /**
     * 裁剪到指定大小
     *
     * @param size 空间大小
     */
    void truncate(long size) throws IOException;

    /**
     * 读取一个int val
     *
     * @return val
     */
    int readInt() throws IOException;

    /**
     * 从文件中读取长度为bytes.length的内容到byte数组
     *
     * @param bytes byte arr
     */
    void read(byte[] bytes) throws IOException;

    /**
     * 强制刷新到磁盘
     *
     * @throws IOException E
     */
    void flush() throws IOException;

    /**
     * 关闭文件
     *
     * @throws IOException e
     */
    void close() throws IOException;

    /**
     * 文件中追加指定字节数组
     *
     * @param bytes append bytes
     */
    void write(byte[] bytes) throws IOException;

    /**
     * 读取一个长整数
     *
     * @return long
     */
    long readLong() throws IOException;

    void writeLong(long l) throws IOException;

    InputStream inputStream(long start) throws IOException;
}
