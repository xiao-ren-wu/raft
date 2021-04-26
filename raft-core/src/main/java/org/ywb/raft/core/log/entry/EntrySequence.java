package org.ywb.raft.core.log.entry;

import org.ywb.raft.core.log.entry.Entry;
import org.ywb.raft.core.log.entry.EntryMeta;

import java.io.IOException;
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
     * 该方法的主要作用是,把日志缓冲中的日志写入日志文件和日志条目索引中
     * 不过，由于raft算法的特性，启动时把commitIndex设定为0，理论上可能新的commitIndex仍在文件中
     * 比如集群中的Follower节点突然重启，假设重启这段时间没有新日志，Follower节点仍然收到来自Leader节点
     * 的心跳信息并更新自己的commitIndex，很明显此时的commitIndex仍在文件中，对于commit方法来说
     * 此时只需要更新自己的commitIndex即可。
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
    void close() throws IOException;
}
