package org.ywb.raft.core.log.entry;

import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/4/25 10:20 下午 星期日
 * @since 1.0.0
 */
@Getter
@ToString
public class EntryIndexItem {

    private final EntryMeta entryMeta;

    private final long offset;

    public EntryIndexItem(int index, long offset, int kind, int term) {
        this.offset = offset;
        this.entryMeta = new EntryMeta(kind, index, term);
    }

    public EntryMeta toEntryMeta() {
        return this.entryMeta;
    }
}
