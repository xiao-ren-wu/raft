package org.ywb.raft.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.log.entry.EntriesFile;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.GeneralEntry;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.utils.ByteArraySeekAbleFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author yuwenbo1
 * @date 2021/5/6 7:49 上午 星期四
 * @since 1.0.0
 */
public class EntriesFileTest {

    @Test
    public void testAppendEntry() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = new ByteArraySeekAbleFile();
        EntriesFile file = new EntriesFile(seekAbleFile);
        Assertions.assertEquals(0L, file.appendEntry(new NoOpEntry(2, 3)));
        seekAbleFile.seek(0);

        // kind
        Assertions.assertEquals(Entry.KIND_NO_OP, seekAbleFile.readInt());
        // index
        Assertions.assertEquals(2,seekAbleFile.readInt());
        // term
        Assertions.assertEquals(3,seekAbleFile.readInt());
        // command bytes
        Assertions.assertEquals(0,seekAbleFile.readInt());

        byte[] commandBytes = "test".getBytes(StandardCharsets.UTF_8);
        Assertions.assertEquals(20L,file.appendEntry(new GeneralEntry(3,3,commandBytes)));
        seekAbleFile.seek(20);
        Assertions.assertEquals(Entry.KIND_GENERAL,seekAbleFile.readInt());
        Assertions.assertEquals(3,seekAbleFile.readInt());
        Assertions.assertEquals(3,seekAbleFile.readInt());
        Assertions.assertEquals(4,seekAbleFile.readInt());
        byte[] buf = new byte[4];
        seekAbleFile.read(buf);
        Assertions.assertArrayEquals(commandBytes,buf);
    }

    @Test
    public void testLoadEntry() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = new ByteArraySeekAbleFile();
        EntriesFile file = new EntriesFile(seekAbleFile);
        Assertions.assertEquals(0L,file.appendEntry(new NoOpEntry(2,3)));
        Assertions.assertEquals(20L,file.appendEntry(new GeneralEntry(3,3,"test".getBytes(StandardCharsets.UTF_8))));
        Assertions.assertEquals(44L,file.appendEntry(new GeneralEntry(4,3,"foo".getBytes(StandardCharsets.UTF_8))));
        Entry entry = file.loadEntry(0);
        Assertions.assertEquals(Entry.KIND_NO_OP,entry.getKind());
        Assertions.assertEquals(3,entry.getTerm());
        Assertions.assertEquals(3,entry.getTerm());
        entry = file.loadEntry(44L);
        Assertions.assertEquals(Entry.KIND_GENERAL,entry.getKind());
        Assertions.assertEquals(4,entry.getIndex());
        Assertions.assertEquals(3,entry.getTerm());
        Assertions.assertArrayEquals("foo".getBytes(StandardCharsets.UTF_8),entry.getCommandBytes());
    }

    @Test
    public void testTruncate() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = new ByteArraySeekAbleFile();
        EntriesFile file = new EntriesFile(seekAbleFile);
        file.appendEntry(new NoOpEntry(2,3));
        Assertions.assertTrue(seekAbleFile.size()>0);
        file.truncate(0L);
        Assertions.assertEquals(0,seekAbleFile);
    }
}
