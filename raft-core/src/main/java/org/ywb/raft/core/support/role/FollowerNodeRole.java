package org.ywb.raft.core.support.role;

import lombok.Getter;
import lombok.ToString;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:29 上午 星期四
 * @since 1.0.0
 */
@ToString
public class FollowerNodeRole extends AbstractNodeRole {

    @Getter
    private final NodeId votedFor;

    @Getter
    private final NodeId leaderId;

    private final ElectionTimeoutTask electionTimeout;

    public FollowerNodeRole(int term, NodeId votedFor, NodeId leaderId, ElectionTimeoutTask electionTimeout) {
        super(RoleName.FOLLOWER, term);
        this.votedFor = votedFor;
        this.leaderId = leaderId;
        this.electionTimeout = electionTimeout;
    }

    /**
     * 获取node节点中的leaderId
     *
     * @param nodeId nodeId
     * @return leader Node id
     */
    @Override
    public NodeId getLeaderId(NodeId nodeId) {
        return leaderId;
    }


    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }
}
