package org.ywb.raft.core.support.role;

import lombok.ToString;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.schedule.task.LogReplicationTask;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:37 上午 星期四
 * @since 1.0.0
 */
@ToString
public class LeaderNodeRole extends AbstractNodeRole {

    /**
     * 日志复制定时器
     */
    private final LogReplicationTask logReplicationTask;

    public LeaderNodeRole(int term, LogReplicationTask logReplicationTask) {
        super(RoleName.LEADER, term);
        this.logReplicationTask = logReplicationTask;
    }

    @Override
    public void cancelTimeoutOrTask() {
        logReplicationTask.cancel();
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return selfId;
    }

}
