package org.ywb.raft.core.support.role;

import lombok.Getter;
import lombok.ToString;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.schedule.task.ElectionTimeoutTask;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/8 8:32 上午 星期四
 * @since 1.0.0
 */
@ToString
public class CandidateNodeRole extends AbstractNodeRole {

    @Getter
    private final int votesCount;

    private final ElectionTimeoutTask electionTimeout;

    public CandidateNodeRole(int term, int votesCount, ElectionTimeoutTask electionTimeout) {
        super(RoleName.CANDIDATE, term);
        this.votesCount = votesCount;
        this.electionTimeout = electionTimeout;
    }

    public CandidateNodeRole(int term, ElectionTimeoutTask electionTimeoutTask) {
        this(term,1,electionTimeoutTask);
    }

    public CandidateNodeRole increaseVotesCount(ElectionTimeoutTask electionTimeout) {
        this.electionTimeout.cancel();
        return new CandidateNodeRole(term, votesCount + 1, electionTimeout);
    }

    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return null;
    }

}
