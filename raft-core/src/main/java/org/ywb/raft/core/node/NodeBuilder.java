package org.ywb.raft.core.node;

import com.google.common.eventbus.EventBus;
import org.ywb.raft.core.rpc.Connector;
import org.ywb.raft.core.schedule.DefaultScheduler;
import org.ywb.raft.core.schedule.Scheduler;
import org.ywb.raft.core.support.*;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeGroup;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yuwenbo1
 * @date 2021/4/11 1:04 下午 星期日
 * @since 1.0.0
 */
public class NodeBuilder {

    private final NodeGroup group;

    private final NodeId selfId;

    private final EventBus eventBus;

    private NodeStore nodeStore;

    private Scheduler scheduler;

    private Connector connector;

    private TaskExecutor taskExecutor = null;

    public NodeBuilder(NodeEndpoint endpoint) {
        this(Collections.singletonList(endpoint), endpoint.getNodeId());
    }

    public NodeBuilder(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.group = new NodeGroup(selfId, endpoints);
        this.selfId = selfId;
        this.eventBus = new EventBus(selfId.getVal());
    }

    public NodeBuilder setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public NodeBuilder setConnector(Connector connector) {
        this.connector = connector;
        return this;
    }

    public NodeBuilder setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        return this;
    }

    public NodeBuilder setNodeStore() {
        return this;
    }

    public Node build() {
        return new NodeImpl(buildContext());
    }

    private NodeContext buildContext() {
        NodeContext context = new NodeContext();
        context.setNodeGroup(group);
        context.setSelfId(selfId);
        context.setEventBus(eventBus);
        context.setConnector(connector);
        context.setNodeStore(nodeStore != null ? nodeStore : new FileNodeStore());
        context.setScheduler(scheduler != null ? scheduler : new DefaultScheduler(5000, 10000, 500, 1000));
        context.setTaskExecutor(taskExecutor != null ? taskExecutor : new SingleThreadTaskExecutor("node"));
        return context;
    }
}
