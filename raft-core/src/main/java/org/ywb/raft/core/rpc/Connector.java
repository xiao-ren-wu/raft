package org.ywb.raft.core.rpc;

import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.msg.AppendEntriesResult;
import org.ywb.raft.core.support.msg.AppendEntriesRpc;
import org.ywb.raft.core.support.msg.RequestVoteResult;
import org.ywb.raft.core.support.msg.RequestVoteRpc;

import java.util.Collection;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:28 下午 星期三
 * @since 1.0.0
 * RPC通讯组件
 */
public interface Connector {
    /**
     * 初始化，系统启动调用
     */
    void initialize();

    /**
     * 发送requestVote消息给多个节点
     *
     * @param rpc                  {@link RequestVoteRpc}
     * @param destinationEndpoints receive request vote node list
     */
    void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints);

    /**
     * 回复RequestVote结果给单个节点
     *
     * @param result              vote result
     * @param destinationEndpoint destinationEndpoint
     */
    void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint);

    /**
     * 发送AppendEntries消息给单个节点
     *
     * @param rpc                 AppendEntriesRpc
     * @param destinationEndpoint destinationEndpoint
     */
    void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint);

    /**
     * 响应AppendEntriesRpc
     *
     * @param result              result
     * @param destinationEndpoint destinationEndpoint
     */
    void replyEntries(AppendEntriesResult result, NodeEndpoint destinationEndpoint);

    /**
     * 关闭连接器，系统关闭调用
     */
    void close();
}
