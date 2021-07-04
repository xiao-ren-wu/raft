package org.ywb.raft.core.log;

import com.google.common.eventbus.EventBus;
import org.ywb.raft.core.log.dir.LogGeneration;
import org.ywb.raft.core.log.dir.RootDir;
import org.ywb.raft.core.log.sequence.FileEntrySequence;
import org.ywb.raft.core.utils.Assert;

import java.io.File;

/**
 * @author yuwenbo1
 * @date 2021/4/27 7:41 上午 星期二
 * @since 1.0.0
 */
public class FileLog extends AbstractLog {

    /*
      根目录下存在多个日志的分代
      文件夹中名称的数字是日志索引偏移量，第一个日志代因为logIndexOffset为1，所以对应的文件夹log-1
      日志代的日志 索引偏移量越大，表示此日志代越新，划分为多个分代主要是为了之后的日志快照

        log-root
            |-log-1
            |   |-entries.bin
            |   |-entries.idx
            |-log-2
            |   |-entries.bin
            |   |-entries.idx

             ...

            |
            |-log-100
                |-entries.bin
                |-entries.idx
     */

    /**
     * 根目录
     */
    private final RootDir rootDir;

    public FileLog(File baseDir, EventBus eventBus) {
        super(eventBus);
        mkdirIfBaseDirNotExist(baseDir);
        rootDir = new RootDir(baseDir);
        LogGeneration latestGeneration = rootDir.getLatestGeneration();
        if (latestGeneration != null) {
            // 日志存在
            entrySequence = new FileEntrySequence(latestGeneration, latestGeneration.getLogIndexOffset());
        } else {
            LogGeneration firstGeneration = rootDir.createFirstGeneration();
            entrySequence = new FileEntrySequence(firstGeneration, 1);
        }
    }

    private void mkdirIfBaseDirNotExist(File baseDir) {
        if (baseDir.isFile()) {
            throw new IllegalArgumentException("base dir must a directory but " + baseDir + " is a file");
        }
        if (!baseDir.exists()) {
            Assert.isTrue(baseDir.mkdir(), "try to create log base directory failed");
        }
    }
}
