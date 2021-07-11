package org.ywb.raft.core.log.entry;

import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:57 下午 星期四
 * @since 1.0.0
 */
public class NoOpEntry extends AbstractEntry {

    public NoOpEntry(int index, int term) {
        super(Entry.KIND_NO_OP, index, term);
    }

    @Override
    public byte[] getCommandBytes() {
        return null;
    }

    @Override
    public String toString() {
        return "NoOpEntry{" +
                "kind=" + kind +
                ", index=" + index +
                ", term=" + term +
                '}';
    }
}
