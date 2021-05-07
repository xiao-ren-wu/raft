package org.ywb.raft.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.index.EntryIndexFile;
import org.ywb.raft.core.log.index.EntryIndexItem;
import org.ywb.raft.core.utils.ByteArraySeekAbleFile;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author yuwenbo1
 * @date 2021/5/6 8:19 上午 星期四
 * @since 1.0.0
 */
public class EntryIndexFileTest {

    private ByteArraySeekAbleFile makeEntryIndexFileContent(int minEntryIndex, int maxEntryIndex) throws IOException {
        ByteArraySeekAbleFile seekAbleFile = new ByteArraySeekAbleFile();
        seekAbleFile.writeInt(minEntryIndex);
        seekAbleFile.writeInt(maxEntryIndex);
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            seekAbleFile.writeLong(10L * i);
            seekAbleFile.writeInt(1);
            seekAbleFile.writeInt(i);
        }
        seekAbleFile.seek(0L);
        return seekAbleFile;
    }

    @Test
    public void testLoad() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = makeEntryIndexFileContent(3, 4);
        EntryIndexFile file = new EntryIndexFile(seekAbleFile);
        Assertions.assertEquals(3, file.getMinEntryIndex());
        Assertions.assertEquals(4, file.getMaxEntryIndex());
        Assertions.assertEquals(2, file.getEntryIndexCount());
        EntryIndexItem item = file.get(3);
        Assertions.assertEquals(30L, item.getOffset());
        Assertions.assertEquals(1, item.getKind());
        Assertions.assertEquals(3, item.getTerm());
        item = file.get(4);
        Assertions.assertEquals(40L, item.getOffset());
        Assertions.assertEquals(1, item.getKind());
        Assertions.assertEquals(4, item.getTerm());
    }

    @Test
    public void testAppendEntryIndex() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = new ByteArraySeekAbleFile();
        EntryIndexFile file = new EntryIndexFile(seekAbleFile);
        file.appendEntryIndex(10, 100L, Entry.KIND_GENERAL, 2);
        Assertions.assertEquals(1, file.getEntryIndexCount());
        Assertions.assertEquals(10, file.getMinEntryIndex());
        Assertions.assertEquals(10, file.getMaxEntryIndex());
        seekAbleFile.seek(0L);

        // min entry index
        Assertions.assertEquals(10,seekAbleFile.readInt());
        // max entry index
        Assertions.assertEquals(10, seekAbleFile.readInt());
        // offset
        Assertions.assertEquals(100L, seekAbleFile.readLong());
        // kind
        Assertions.assertEquals(1, seekAbleFile.readInt());
        // term
        Assertions.assertEquals(2, seekAbleFile.readInt());

        EntryIndexItem entryIndexItem = file.get(10);
        Assertions.assertNotNull(entryIndexItem);
        Assertions.assertEquals(100L, entryIndexItem.getOffset());
        Assertions.assertEquals(1, entryIndexItem.getKind());
        Assertions.assertEquals(2, entryIndexItem.getTerm());
        file.appendEntryIndex(11, 200L, 1, 2);

        Assertions.assertEquals(2, file.getEntryIndexCount());
        Assertions.assertEquals(10, file.getMinEntryIndex());
        Assertions.assertEquals(11, file.getMaxEntryIndex());
    }

    @Test
    public void testClear() throws Exception {
        ByteArraySeekAbleFile seekAbleFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile file = new EntryIndexFile(seekAbleFile);
        Assertions.assertFalse(file.isEmpty());
        file.clear();
        Assertions.assertTrue(file.isEmpty());
        Assertions.assertEquals(0, file.getEntryIndexCount());
        Assertions.assertEquals(0, seekAbleFile.size());
    }


    @Test
    public void testRemoveAfter() throws IOException {
        ByteArraySeekAbleFile seekAbleFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile file = new EntryIndexFile(seekAbleFile);
        long oldSize = seekAbleFile.size();
        file.removeAfter(6);
        Assertions.assertEquals(5, file.getMinEntryIndex());
        Assertions.assertEquals(6, file.getMaxEntryIndex());
        Assertions.assertEquals(2, file.getEntryIndexCount());
    }

    @Test
    public void testGet() throws IOException {
        EntryIndexFile file = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        EntryIndexItem entryIndexItem = file.get(3);
        Assertions.assertNotNull(entryIndexItem);
        Assertions.assertEquals(1, entryIndexItem.getKind());
        Assertions.assertEquals(3, entryIndexItem.getTerm());
    }

    @Test
    public void testIterator() throws IOException {
        EntryIndexFile file = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        Iterator<EntryIndexItem> iterator = file.iterator();
        Assertions.assertTrue(iterator.hasNext());
        EntryIndexItem item = iterator.next();
        Assertions.assertEquals(3,item.getIndex());
        Assertions.assertEquals(1,item.getKind());
        Assertions.assertTrue(iterator.hasNext());
        EntryIndexItem next = iterator.next();
        Assertions.assertFalse(iterator.hasNext());
    }
}
