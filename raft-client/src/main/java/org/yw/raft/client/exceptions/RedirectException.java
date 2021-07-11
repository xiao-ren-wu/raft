package org.yw.raft.client.exceptions;

import lombok.Getter;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/7/11 11:08 上午 星期日
 * @since 1.0.0
 */
@Getter
public class RedirectException extends RuntimeException{

    private NodeId leaderId;

    public RedirectException(NodeId leaderId) {
        this.leaderId = leaderId;
    }
}
