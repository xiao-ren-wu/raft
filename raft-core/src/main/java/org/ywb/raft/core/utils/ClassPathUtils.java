package org.ywb.raft.core.utils;

import java.io.InputStream;

/**
 * @author yuwenbo1
 * @date 2021/7/3 6:49 下午 星期六
 * @since 1.0.0
 */
public class ClassPathUtils {

    public static InputStream getResourceStream(String filename) {
        return ClassPathUtils
                .class
                .getClassLoader()
                .getResourceAsStream(filename);
    }

    public static String getClassPath() {
        return ClassPathUtils
                .class
                .getClassLoader()
                .getResource("")
                .getPath();
    }

}
