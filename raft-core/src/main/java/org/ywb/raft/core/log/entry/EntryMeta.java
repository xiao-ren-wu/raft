package org.ywb.raft.core.log.entry;

import lombok.Getter;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:53 下午 星期四
 * @since 1.0.0
 */
@Getter
public class EntryMeta {

    private final int kind;

    private final int index;

    private final int term;

    public EntryMeta(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    /**
     * 服务第一次启动时内存和文件中的entry都为空
     *
     * @return first entry meta
     */
    public static EntryMeta firstEntryMeta() {
        return new EntryMeta(Entry.KIND_NO_OP, 1, -1);
    }
}
