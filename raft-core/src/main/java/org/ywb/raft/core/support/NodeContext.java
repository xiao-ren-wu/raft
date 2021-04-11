package org.ywb.raft.core.support;

import com.google.common.eventbus.EventBus;
import lombok.Data;
import org.ywb.raft.core.node.NodeStore;
import org.ywb.raft.core.rpc.Connector;
import org.ywb.raft.core.schedule.Scheduler;
import org.ywb.raft.core.support.meta.GroupMember;
import org.ywb.raft.core.support.meta.NodeGroup;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/7 11:22 下午 星期三
 * @since 1.0.0
 */
@Data
public class NodeContext {

    /**
     * 当前节点
     */
    private NodeId selfId;

    /**
     * 成员列表
     */
    private NodeGroup nodeGroup;

    /**
     * 日志
     */
    /// private Log log;
    /**
     * RPC组件
     */
    private Connector connector;

    /**
     * 定时器组件
     */
    private Scheduler scheduler;

    /**
     * pub/sub
     */
    private EventBus eventBus;

    /**
     * 主线程执行器
     */
    private TaskExecutor taskExecutor;

    /**
     * 部分角色状态数据存储
     */
    private NodeStore nodeStore;

    public GroupMember findMember(NodeId nodeId) {
        return nodeGroup.findGroupMember(nodeId);
    }
}
