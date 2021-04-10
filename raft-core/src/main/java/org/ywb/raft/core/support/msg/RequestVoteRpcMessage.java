package org.ywb.raft.core.support.msg;

import lombok.Data;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 6:14 下午 星期六
 * @since 1.0.0
 */
@Data
public class RequestVoteRpcMessage {

    private RequestVoteRpc rpc;

    private NodeId sourceNodeId;

    public RequestVoteRpc get() {
        return rpc;
    }
}
