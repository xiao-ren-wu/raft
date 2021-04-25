package org.ywb.raft.core.log.entry;

/**
 * @author yuwenbo1
 * @date 2021/4/25 10:14 下午 星期日
 * @since 1.0.0
 */
public class EntryFactory {

    public static Entry create(int kind, int index, int term, byte[] commandBytes) {
        switch (kind) {
            case Entry.KIND_GENERAL:
                return new GeneralEntry(index, term, commandBytes);
            case Entry.KIND_NO_OP:
                return new NoOpEntry(index, term);
            default:
                throw new IllegalArgumentException(String.format("kind %s not found", kind));
        }
    }
}
