package org.ywb.raft.core.exceptions;

import java.io.IOException;

/**
 * @author yuwenbo1
 * @date 2021/4/11 5:07 下午 星期日
 * @since 1.0.0
 */
public class NodeStoreException extends RuntimeException {
    public NodeStoreException(IOException e) {
        super(e);
    }
}
