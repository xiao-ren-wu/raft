package org.ywb.raft.core.support.role;

import lombok.Getter;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:27 上午 星期四
 * @since 1.0.0
 */
@Getter
public abstract class AbstractNodeRole {

    private final RoleName name;

    protected final int term;

    public AbstractNodeRole(RoleName name, int term) {
        this.name = name;
        this.term = term;
    }

    /**
     * 取消超时或者定时任务
     */
    public abstract void cancelTimeoutOrTask();

}
