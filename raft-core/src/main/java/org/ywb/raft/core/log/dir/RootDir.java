package org.ywb.raft.core.log.dir;

import org.ywb.raft.core.utils.Assert;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuwenbo1
 * @date 2021/4/27 7:52 上午 星期二
 * @since 1.0.0
 */
public class RootDir {

    private static final String FIRST_DIR = "log-1";

    private final File rootDir;

    public RootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public LogGeneration getLatestGeneration() {
        Assert.isTrue(Objects.nonNull(rootDir) && rootDir.isDirectory(), () -> new IllegalArgumentException("rootDir is Illegal " + rootDir));
        File[] files = rootDir.listFiles(File::isDirectory);
        if (Objects.isNull(files)||files.length==0) {
            return null;
        }
        File file = Stream.of(files)
                .sorted(this::fileNameSort)
                .collect(Collectors.toList())
                .get(0);
        return new LogGeneration(file);
    }

    private int fileNameSort(File file, File file1) {
        int fileNo = getFileNo(file);
        int fileNo1 = getFileNo(file1);
        return fileNo1 - fileNo;
    }

    private int getFileNo(File file) {
        return Integer.parseInt(file.getName().split("-")[1]);
    }

    public LogGeneration createFirstGeneration() {
        String absolutePath = rootDir.getAbsolutePath();
        String firstDir = String.format("%s/%s", absolutePath, FIRST_DIR);
        LogGeneration firstLogGeneration = new LogGeneration(new File(firstDir));
        firstLogGeneration.initialize();
        return firstLogGeneration;
    }


}
