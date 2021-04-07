package org.ywb.raft.core.support;

import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:27 下午 星期三
 * @since 1.0.0
 * 部分角色状态数据存储
 */
public interface NodeStore {

    /**
     * 获取currentTerm
     *
     * @return current term
     */
    int getTerm();

    /**
     * 设置currentTerm
     *
     * @param term term
     */
    void setTerm(int term);

    /**
     * 获取voteFor
     *
     * @return NodeId
     */
    NodeId getVotedFor();

    /**
     * 设置voteFor
     *
     * @param nodeId nodeId
     */
    void setVotedFor(NodeId nodeId);

    /**
     * 关闭文件
     */
    void close();
}
