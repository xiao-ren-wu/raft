package org.ywb.raft.core.exceptions;

import java.io.IOException;

/**
 * @author yuwenbo1
 * @date 2021/4/26 8:22 上午 星期一
 * @since 1.0.0
 */
public class LogException extends RuntimeException {
    public LogException(String s, IOException e) {
        super(s,e);
    }

    public LogException(IOException e) {
        super(e);
    }
}
