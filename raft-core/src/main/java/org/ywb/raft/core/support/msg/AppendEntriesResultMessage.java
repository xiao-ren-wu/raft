package org.ywb.raft.core.support.msg;

import lombok.Data;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/10 9:05 下午 星期六
 * @since 1.0.0
 */
@Data
public class AppendEntriesResultMessage {

    private AppendEntriesResult appendEntriesResult;

    private NodeId sourceNodeId;

    public AppendEntriesResult get() {
        return appendEntriesResult;
    }
}
