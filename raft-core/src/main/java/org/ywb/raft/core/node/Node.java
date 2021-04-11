package org.ywb.raft.core.node;

import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.support.role.AbstractNodeRole;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:22 上午 星期四
 * @since 1.0.0
 */
public interface Node extends NodeExecutor, NodeTask {
    /**
     * 获取nodeContext
     *
     * @return nodeContext
     */
    NodeContext getContext();

    /**
     * 获取node role
     *
     * @return role
     */
    AbstractNodeRole getRole();

    /**
     * 改变node角色
     *
     * @param nodeRole role
     */
    void changeToRole(AbstractNodeRole nodeRole);
}
