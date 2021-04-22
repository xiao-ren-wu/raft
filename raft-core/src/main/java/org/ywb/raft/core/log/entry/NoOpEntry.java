package org.ywb.raft.core.log.entry;

import lombok.ToString;
import org.ywb.raft.core.log.entry.AbstractEntry;
import org.ywb.raft.core.log.entry.Entry;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:57 下午 星期四
 * @since 1.0.0
 */
@ToString
public class NoOpEntry extends AbstractEntry {

    public NoOpEntry(int index, int term) {
        super(Entry.KIND_NO_OP, index, term);
    }

    @Override
    public byte[] getCommandBytes() {
        return new byte[0];
    }
}