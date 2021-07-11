package org.ywb.raft.core.log.entry;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:55 下午 星期四
 * @since 1.0.0
 */
@Getter
public class GeneralEntry extends AbstractEntry {

    private final byte[] commandBytes;

    public GeneralEntry(int index, int term, byte[] commandBytes) {
        super(Entry.KIND_GENERAL, index, term);
        this.commandBytes = commandBytes;
    }

    @Override
    public byte[] getCommandBytes() {
        return commandBytes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "kind=" + kind +
                ", index=" + index +
                ", term=" + term +
                ", commandBytes=" + Arrays.toString(commandBytes) +
                '}';
    }
}
