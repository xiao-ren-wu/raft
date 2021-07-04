package org.ywb.raft.core.node;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import io.netty.channel.nio.NioEventLoopGroup;
import org.ywb.raft.core.eventbus.OnReceiveSubScribeImpl;
import org.ywb.raft.core.log.FileLog;
import org.ywb.raft.core.log.Log;
import org.ywb.raft.core.rpc.Connector;
import org.ywb.raft.core.rpc.RaftNettyConnector;
import org.ywb.raft.core.schedule.DefaultScheduler;
import org.ywb.raft.core.schedule.Scheduler;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.SingleThreadTaskExecutor;
import org.ywb.raft.core.support.TaskExecutor;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeGroup;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.utils.ClassPathUtils;

import javax.annotation.Nonnull;
import java.io.File;
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

    private Log log;

    private FileNodeStore fileNodeStore;

    private TaskExecutor taskExecutor = null;

    private NioEventLoopGroup workerNioEventLoopGroup = new NioEventLoopGroup();


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

    public NodeBuilder setWorkerNioEventLoopGroup(@Nonnull NioEventLoopGroup workerNioEventLoopGroup) {
        Preconditions.checkNotNull(workerNioEventLoopGroup);
        this.workerNioEventLoopGroup = workerNioEventLoopGroup;
        return this;
    }

    public NodeBuilder setLog(Log log) {
        this.log = log;
        return this;
    }

    public NodeBuilder setNodeStore() {
        return this;
    }

    public Node build() {
        NodeImpl node = new NodeImpl(buildContext());
        new OnReceiveSubScribeImpl(node);
        return node;
    }

    private NodeContext buildContext() {
        NodeContext context = new NodeContext();
        context.setNodeGroup(group);
        context.setSelfId(selfId);
        context.setLog(log);
        context.setEventBus(eventBus);
        context.setConnector(connector != null ? connector : createNioConnector());
        context.setNodeStore(nodeStore != null ? nodeStore : new FileNodeStore());
        context.setScheduler(scheduler != null ? scheduler : new DefaultScheduler(5000, 10000, 500, 1000));
        context.setTaskExecutor(taskExecutor != null ? taskExecutor : new SingleThreadTaskExecutor("node"));
        return context;
    }

    public NodeBuilder setDataDir(String dataDirPath) {
        if (dataDirPath == null || "".equals(dataDirPath)) {
            // use classpath
            dataDirPath = String.format("%s/%s", ClassPathUtils.getClassPath(), selfId.getVal());
        }
        File dataDir = new File(dataDirPath);
        log = new FileLog(dataDir, eventBus);
        fileNodeStore = new FileNodeStore(new File(dataDir, FileNodeStore.FILE_NAME));
        return this;
    }

    @Nonnull
    private RaftNettyConnector createNioConnector() {
        int port = group.findSelf().getEndpoint().getAddress().getPort();
        return new RaftNettyConnector(workerNioEventLoopGroup, true, selfId, eventBus, port);
    }


}
