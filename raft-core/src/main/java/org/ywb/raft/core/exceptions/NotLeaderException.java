package org.ywb.raft.core.exceptions;

import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.support.meta.NodeEndpoint;

/**
 * @author yuwenbo1
 * @date 2021/6/1 7:32 上午 星期二
 * @since 1.0.0
 */
public class NotLeaderException extends RuntimeException {
    public NotLeaderException(RoleName roleName, NodeEndpoint endpoint) {
        super(roleName.name()+endpoint);
    }
}
