package org.ywb.raft.core.rpc.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 6:14 下午 星期六
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestVoteRpcMessage {

    private RequestVoteRpc rpc;

    private NodeId sourceNodeId;

    public RequestVoteRpc get() {
        return rpc;
    }
}
