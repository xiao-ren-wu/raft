package org.ywb.raft.core.log.dir;

import java.io.File;
import java.io.IOException;

/**
 * @author yuwenbo1
 * @date 2021/4/26 8:15 上午 星期一
 * @since 1.0.0
 */
public interface LogDir {

    /**
     * 初始化目录
     */
    void initialize();

    /**
     * 是否存在
     *
     * @return bool
     */
    boolean exists();

    /**
     * 获取entries对应的文件
     *
     * @return file
     */
    File getEntriesFile() throws IOException;

    /**
     * 获取entryIndexFile
     *
     * @return index file
     */
    File getEntryOffsetIndexFile() throws IOException;

    /**
     * 获取目录
     *
     * @return file catalog
     */
    File get();

    /**
     * 重命名目录
     *
     * @param logDir new log dir
     * @return bool
     */
    boolean renameTo(LogDir logDir);
}
