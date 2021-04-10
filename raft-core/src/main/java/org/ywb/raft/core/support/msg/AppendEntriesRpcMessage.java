package org.ywb.raft.core.support.msg;

import lombok.Data;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 8:28 下午 星期六
 * @since 1.0.0
 */
@Data
public class AppendEntriesRpcMessage {

    private NodeId sourceNodeId;

    private AppendEntriesRpc appendEntriesRpc;

    public AppendEntriesRpc get() {
        return appendEntriesRpc;
    }
}
