package org.ywb.raft.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.log.entry.EntriesFile;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.log.index.EntryIndexFile;
import org.ywb.raft.core.log.sequence.FileEntrySequence;
import org.ywb.raft.core.utils.ByteArraySeekAbleFile;

import java.io.IOException;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/5/7 9:18 下午 星期五
 * @since 1.0.0
 */
public class FileEntrySequenceTest {

    private EntriesFile entriesFile;

    private EntryIndexFile entryIndexFile;

    @BeforeEach
    public void setUp() throws IOException {
        entriesFile = new EntriesFile(new ByteArraySeekAbleFile());
        entryIndexFile = new EntryIndexFile(new ByteArraySeekAbleFile());
    }

    @Test
    public void testInitialize() throws IOException {
        entryIndexFile.appendEntryIndex(1, 0, 1, 1);
        entryIndexFile.appendEntryIndex(2, 20L, 1, 1);
        FileEntrySequence fileEntrySequence = new FileEntrySequence(1, entriesFile, entryIndexFile);
        Assertions.assertEquals(3, fileEntrySequence.getNextLogIndex());
        Assertions.assertEquals(1, fileEntrySequence.getFirstLogIndex());
        Assertions.assertEquals(2, fileEntrySequence.getLastLogIndex());
        Assertions.assertEquals(2, fileEntrySequence.getCommitIndex());
    }

    @Test
    public void testAppendEntry() {
        FileEntrySequence sequence = new FileEntrySequence(1, entriesFile, entryIndexFile);
        Assertions.assertEquals(1, sequence.getNextLogIndex());
        sequence.append(new NoOpEntry(1, 1));
        Assertions.assertEquals(2, sequence.getNextLogIndex());
        Assertions.assertEquals(1, sequence.getLastEntry().getIndex());

    }

    private void appendEntryToFile(Entry entry) throws IOException {
        long offset = entriesFile.appendEntry(entry);
        entryIndexFile.appendEntryIndex(entry.getIndex(), offset, entry.getKind(), entry.getTerm());
    }

    @Test
    public void testGetEntry() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        FileEntrySequence sequence = new FileEntrySequence(1, entriesFile, entryIndexFile);
        sequence.append(new NoOpEntry(2, 1));
        Assertions.assertNull(sequence.getEntry(0));
        Assertions.assertEquals(1, sequence.getEntry(1).getIndex());
        Assertions.assertEquals(2, sequence.getEntry(2).getIndex());
        Assertions.assertNull(sequence.getEntry(3));

    }

    @Test
    public void testSubList() throws IOException {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 2));
        FileEntrySequence sequence = new FileEntrySequence(1, entriesFile, entryIndexFile);
        sequence.append(new NoOpEntry(sequence.getNextLogIndex(), 3));
        sequence.append(new NoOpEntry(sequence.getNextLogIndex(), 4));

        List<Entry> subList = sequence.subView(2);
        Assertions.assertEquals(3, subList.size());
        Assertions.assertEquals(2, subList.get(0).getIndex());
        Assertions.assertEquals(4, subList.get(2).getIndex());
    }

    @Test
    public void testRemoveAfterEntriesInFile() throws Exception {
        appendEntryToFile(new NoOpEntry(1, 1));
        appendEntryToFile(new NoOpEntry(2, 1));
        FileEntrySequence sequence = new FileEntrySequence(1, entriesFile, entryIndexFile);
        sequence.append(new NoOpEntry(3, 2));
        Assertions.assertEquals(1, sequence.getFirstLogIndex());
        Assertions.assertEquals(3, sequence.getLastLogIndex());
        sequence.removeAfter(1);
        Assertions.assertEquals(1, sequence.getFirstLogIndex());
        Assertions.assertEquals(1, sequence.getLastLogIndex());
    }


}
