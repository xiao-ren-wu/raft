package org.ywb.raft.core.rpc.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 8:28 下午 星期六
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppendEntriesRpcMessage {

    private NodeId sourceNodeId;

    private AppendEntriesRpc appendEntriesRpc;

    public AppendEntriesRpc get() {
        return appendEntriesRpc;
    }
}
