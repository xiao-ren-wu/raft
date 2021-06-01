package org.ywb.raft.core.node.support;

import lombok.Getter;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.support.meta.NodeId;

import javax.annotation.concurrent.Immutable;

/**
 * @author yuwenbo1
 * @date 2021/6/1 7:28 上午 星期二
 * @since 1.0.0
 */
@Getter
@Immutable
public class RoleNameAndLeaderId {
    private final RoleName roleName;
    private final NodeId leaderNodeId;

    public RoleNameAndLeaderId(RoleName roleName, NodeId leaderNodeId) {
        this.roleName = roleName;
        this.leaderNodeId = leaderNodeId;
    }
}
