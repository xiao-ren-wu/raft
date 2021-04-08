package org.ywb.raft.core.support.msg;

import lombok.Data;
import org.ywb.raft.core.support.log.Entry;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Collections;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/6 11:30 下午 星期二
 * @since 1.0.0
 */
@Data
public class AppendEntriesRpc {

    /**
     * 选举term
     */
    private int term;

    /**
     * leader节点id
     */
    private NodeId leaderId;

    /**
     * 前一条日志索引
     */
    private int prevLogIndex = 0;

    /**
     * 前一条日志的term
     */
    private int prevLogTerm;

    /**
     * 复制的日志条目
     */
    private List<Entry> entries = Collections.emptyList();

    /**
     * leader的commitIndex
     */
    private int leaderCommit;

}