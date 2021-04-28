package org.ywb.raft.core.rpc.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 9:05 下午 星期六
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppendEntriesResultMessage {

    private AppendEntriesResult appendEntriesResult;

    private NodeId sourceNodeId;

    private AppendEntriesRpc appendEntriesRpc;

    public AppendEntriesResult get() {
        return appendEntriesResult;
    }

    public AppendEntriesRpc getRpc(){
        return appendEntriesRpc;
    }
}
