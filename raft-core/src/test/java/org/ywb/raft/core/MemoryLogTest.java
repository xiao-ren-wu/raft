package org.ywb.raft.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ywb.raft.core.log.Log;
import org.ywb.raft.core.log.MemoryLog;
import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/5/7 10:39 下午 星期五
 * @since 1.0.0
 */
public class MemoryLogTest {

    @Test
    public void testCreateAppendEntriesRpcStartFromOne() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.appendEntry(1);
        AppendEntriesRpc rpc = memoryLog.createAppendEntriesRpc(1, new NodeId("A"), 1, Log.ALL_ENTRIES);
        Assertions.assertEquals(1,rpc.getTerm());
        Assertions.assertEquals(0,rpc.getPrevLogIndex());
        Assertions.assertEquals(0,rpc.getPrevLogTerm());
        Assertions.assertEquals(2,rpc.getEntries().size());
        Assertions.assertEquals(1,rpc.getEntries().get(0).getIndex());
    }

    @Test
    public void testAppendEntriesFromLeaderSkip() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.appendEntry(1);
        List<Entry> leaderEntries = Arrays.asList(new NoOpEntry(2, 1), new NoOpEntry(3, 2));
        Assertions.assertTrue(memoryLog.appendEntriesFromLeader(1,1,leaderEntries));
    }

    @Test
    public void testAppEntriesFromLeaderConflict() {
        MemoryLog memoryLog = new MemoryLog();
        memoryLog.appendEntry(1);
        memoryLog.appendEntry(1);
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 2),
                new NoOpEntry(3, 2)
        );
        Assertions.assertTrue(memoryLog.appendEntriesFromLeader(1,1,leaderEntries));
    }
}
