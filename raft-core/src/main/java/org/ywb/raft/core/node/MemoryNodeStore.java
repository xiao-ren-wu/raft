package org.ywb.raft.core.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ywb.raft.core.node.NodeStore;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:52 下午 星期三
 * @since 1.0.0
 * 基于内存级别日志存储，仅适用于测试使用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryNodeStore implements NodeStore {

    private int term;

    private NodeId votedFor;

    @Override
    public void close() {
        // just do nothing
    }
}
