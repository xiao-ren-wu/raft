package org.ywb.raft.core.log;

import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;

import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/22 10:10 下午 星期四
 * @since 1.0.0
 */
public interface EntrySequence {

    /**
     * 判断是否为空
     *
     * @return empty
     */
    boolean isEmpty();

    /**
     * 获取第一条日志索引
     *
     * @return first log index
     */
    int getFirstLogIndex();

    /**
     * 获取最后一条日志索引
     *
     * @return last log index
     */
    int getLastLogIndex();

    /**
     * 获取吓一跳日志索引
     *
     * @return next log index
     */
    int getNextLogIndex();

    /**
     * 获取子序列，到最后一条日志
     *
     * @param fromIndex beginIndex
     * @return sub list
     */
    List<Entry> subList(int fromIndex);

    /**
     * 获取子序列
     *
     * @param fromIndex begin
     * @param toIndex   end
     * @return sub list
     */
    List<Entry> subList(int fromIndex, int toIndex);

    /**
     * 检查某个日志条目是否存在
     *
     * @param index index
     * @return bool
     */
    boolean isEntryPresent(int index);

    /**
     * 获取某个日志的meta
     *
     * @param index index
     * @return meta
     */
    EntryMeta getEntryMeta(int index);

    /**
     * 获取日志节点
     *
     * @param index index
     * @return entry
     */
    Entry getEntry(int index);

    /**
     * 获取最后一条日志节点
     *
     * @return entry
     */
    Entry getLastEntry();

    /**
     * 追加日志条目
     *
     * @param entries entries
     */
    void append(List<Entry> entries);

    /**
     * 追加日志条目
     *
     * @param entry entry
     */
    void append(Entry entry);

    /**
     * 推进commit Index
     *
     * @param index index
     */
    void commit(int index);

    /**
     * 获取当前commit index
     *
     * @return commit index
     */
    int getCommitIndex();

    /**
     * 移除某个索引之后的日志条目
     *
     * @param index remove index
     */
    void removeAfter(int index);

    /**
     * 关闭
     */
    void close();
}
