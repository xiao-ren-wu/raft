package org.ywb.raft.core.support.meta;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ywb.raft.core.support.ReplicatingState;
import org.ywb.raft.core.utils.Assert;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:15 下午 星期一
 * @since 1.0.0
 * 集群成员信息
 */
@Getter
@ToString
public class GroupMember {

    /**
     * 节点元数据信息
     */
    private final NodeEndpoint endpoint;

    /**
     * 日志复制进度
     */
    @Setter
    private ReplicatingState replicatingState;

    @Setter
    private boolean major;

    @Setter
    private boolean removing = false;

    public GroupMember(NodeEndpoint endpoint) {
        // init replicating state
        this(endpoint, null);
    }

    public GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState) {
        this.endpoint = endpoint;
        this.replicatingState = replicatingState;
    }

    public int getNextIndex() {
        return replicatingState.getNextIndex();
    }

    public int getMatchIndex() {
        return replicatingState.getMatchIndex();
    }

    public ReplicatingState ensureReplicatingState() {
        Assert.isTrue(replicatingState != null, () -> new IllegalStateException("replication state not set"));
        return replicatingState;
    }


    public boolean advanceReplicatingState(int lastEntryIndex) {
        return ensureReplicatingState()
                .advance(lastEntryIndex);
    }

    public boolean idEquals(NodeId nodeId) {
        return getId().equals(nodeId);
    }

    public NodeId getId() {
        return this.getEndpoint().getNodeId();
    }

    public boolean backOffNextIndex() {
        return this.replicatingState.backOffNextIndex();
    }

    public void replicateNow() {
        replicateAt(System.currentTimeMillis());
    }

    void replicateAt(long replicatedAt) {
        ReplicatingState replicatingState = ensureReplicatingState();
        replicatingState.setReplicating(true);
        replicatingState.setLastReplicatedAt(replicatedAt);
    }
}
