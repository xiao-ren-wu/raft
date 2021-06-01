package org.ywb.raft.core.node;

import org.ywb.raft.core.node.support.RoleNameAndLeaderId;
import org.ywb.raft.core.support.NodeContext;
import org.ywb.raft.core.statemachine.StateMachine;
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

    /**
     * 注册状态机
     *
     * @param stateMachine 状态机
     */
    void registerStateMachine(StateMachine stateMachine);


    /**
     * 追加日志
     *
     * @param commandBytes log bytes
     */
    void appendLog(byte[] commandBytes);

    /**
     * 通知从节点追加日志
     */
    void doReplicateLog();

    /**
     * 获取role name 和leader node id
     *
     * @return {@link RoleNameAndLeaderId}
     */
    RoleNameAndLeaderId getRoleNameAndLeaderId();
}
