package org.ywb.raft.core;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.role.AbstractNodeRole;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:23 上午 星期四
 * @since 1.0.0
 */
@Slf4j
public class NodeImpl implements Node{

    /**
     * 组件核心上下文
     */
    private final NodeContext context;

    /**
     * 组件是否已经启动
     */
    private boolean started;

    /**
     * 当前角色以及信息
     */
    private AbstractNodeRole role;

    public NodeImpl(NodeContext context) {
        this.context = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() throws InterruptedException {

    }
}
