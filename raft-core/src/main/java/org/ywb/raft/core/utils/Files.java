package org.ywb.raft.core.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author yuwenbo1
 * @since 2021/4/12 17:50
 */
public class Files {
    /**
     * 创建文件，如果文件不存在
     *
     * @param file target file
     */
    public static void createFileIfNotExist(File file) throws IOException {
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            Assert.isTrue(newFile, () -> new IllegalStateException("file: " + file + " create failed"));
        }
    }
}
