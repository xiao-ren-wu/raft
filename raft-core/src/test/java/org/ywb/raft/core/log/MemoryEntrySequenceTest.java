package org.ywb.raft.core.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.log.sequence.MemoryEntrySequence;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/29 10:05 下午 星期四
 * @since 1.0.0
 */
public class MemoryEntrySequenceTest {

    @Test
    public void testAppendEntry() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence();
        memoryEntrySequence.append(new NoOpEntry(1, 1));
        Assertions.assertEquals(2, memoryEntrySequence.getNextLogIndex());
        Assertions.assertEquals(1, memoryEntrySequence.getLastLogIndex());
    }

    @Test
    public void testGetRandEntry() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        Assertions.assertNull(memoryEntrySequence.getEntry(1));
        Assertions.assertEquals(2, memoryEntrySequence.getEntry(2).getIndex());
        Assertions.assertEquals(3, memoryEntrySequence.getEntry(3).getIndex());
        Assertions.assertNull(memoryEntrySequence.getEntry(4));
    }

    @Test
    public void testGetEntryMeta() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        Assertions.assertNull(memoryEntrySequence.getEntry(2));
        memoryEntrySequence.append(new NoOpEntry(2, 1));
        EntryMeta meta = memoryEntrySequence.getEntryMeta(2);
        Assertions.assertEquals(2, meta.getIndex());
        Assertions.assertEquals(1, meta.getTerm());
    }

    @Test
    public void testSubListOneElement() {
        MemoryEntrySequence memoryEntrySequence = new MemoryEntrySequence(2);
        memoryEntrySequence.append(
                Arrays.asList(
                        new NoOpEntry(2, 1),
                        new NoOpEntry(3, 1)));
        List<Entry> subList = memoryEntrySequence.subList(2, 3);
        Assertions.assertEquals(1, subList.size());
        Assertions.assertEquals(2, subList.get(0).getIndex());
    }

    @Test
    public void testRemoveAfterPartial() {
        MemoryEntrySequence sequence = new MemoryEntrySequence(2);
        sequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        sequence.removeAfter(2);
        Assertions.assertEquals(2, sequence.getLastLogIndex());
        Assertions.assertEquals(3, sequence.getNextLogIndex());
    }

}
