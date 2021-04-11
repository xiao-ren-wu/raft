package org.ywb.raft.core.eventbus;

import org.ywb.raft.core.rpc.msg.AppendEntriesResultMessage;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpcMessage;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpcMessage;

/**
 * @author yuwenbo1
 * @date 2021/4/11 4:42 下午 星期日
 * @since 1.0.0
 */
public interface OnReceiveSubscribe {

    /**
     * 当收到投票请求
     *
     * @param requestVoteRpcMessage requestVoteRpcMessage
     */
    void onReceiveRequestVoteRpc(RequestVoteRpcMessage requestVoteRpcMessage);


    /**
     * 当收到投票结果
     *
     * @param result RequestVoteResult
     */
    void onReceiveRequestVoteResult(RequestVoteResult result);


    /**
     * 当收到日志追加请求
     *
     * @param rpcMessage AppendEntriesRpcMessage
     */
    void onReceiveAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage);


    /**
     * 当收到日志追加结果
     *
     * @param resultMessage AppendEntriesResultMessage
     */
    void onReceiveAppendEntriesResult(AppendEntriesResultMessage resultMessage);
}
