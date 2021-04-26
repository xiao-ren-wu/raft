package org.ywb.raft.core.log;

import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;
import org.ywb.raft.core.log.entry.GeneralEntry;
import org.ywb.raft.core.log.entry.NoOpEntry;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:59 下午 星期四
 * @since 1.0.0
 */
public interface Log {

    int ALL_ENTRIES = -1;

    /**
     * 获取最后一条日志元信息
     *
     * @return last meta
     */
    EntryMeta getLastEntryMeta();

    /**
     * 创建AppendEntries消息
     *
     * @param term       term
     * @param selfId     selfId
     * @param nextIndex  nextIndex
     * @param maxEntries maxEntries
     * @return AppendEntriesRpc
     */
    AppendEntriesRpc createAppendEntriesRpc(int term, NodeId selfId, int nextIndex, int maxEntries);

    /**
     * 获取下一条日志索引
     *
     * @return next index
     */
    int getNextIndex();

    /**
     * 获取当前commitIndex
     *
     * @return commitIndex
     */
    int getCommitIndex();

    /**
     * 判断对象的lastLogIndex和lastLogTerm是否比自己新
     *
     * @param lastLogIndex last log index
     * @param lastLogTerm  last log term
     * @return bool
     */
    boolean isNewThan(int lastLogIndex, int lastLogTerm);

    /**
     * 增加一个NO-OP日志
     *
     * @param term term
     * @return NoOpEntry
     */
    NoOpEntry appendEntry(int term);

    /**
     * 增加一个general日志
     *
     * @param term    term
     * @param command command bytes
     * @return General entry
     */
    GeneralEntry appendEntry(int term, byte[] command);

    /**
     * 追加来自leader的日志条目
     *
     * @param prevLogIndex 上一条日志索引
     * @param prevLogTerm  上一条日志term
     * @param leaderEntries      entries
     * @return bool
     */
    boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> leaderEntries);

    /**
     * 推进commitIndex
     *
     * @param newCommitIndex new commit index
     * @param currentTerm    current term
     */
    void advanceCommitIndex(int newCommitIndex, int currentTerm);

    /// void setStateMachine(StateMachine stateMachine);

    /**
     * 关闭
     */
    void close();
}
