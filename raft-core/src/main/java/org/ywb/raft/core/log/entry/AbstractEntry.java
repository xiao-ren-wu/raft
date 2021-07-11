package org.ywb.raft.core.log.entry;


import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:53 下午 星期四
 * @since 1.0.0
 */
@Getter
public abstract class AbstractEntry implements Entry {

    protected final int kind;

    protected final int index;

    protected final int term;

    public AbstractEntry(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    @Override
    public EntryMeta getMeta() {
        return new EntryMeta(kind, index, term);
    }

}
