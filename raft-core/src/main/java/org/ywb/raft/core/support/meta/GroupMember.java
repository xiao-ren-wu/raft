package org.ywb.raft.core.support.meta;

import lombok.Getter;
import org.ywb.raft.core.support.ReplicatingState;
import org.ywb.raft.core.utils.Assert;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:15 下午 星期一
 * @since 1.0.0
 * 集群成员信息
 */
@Getter
public class GroupMember {

    /**
     * 节点元数据信息
     */
    private final NodeEndpoint endpoint;

    /**
     * 日志复制进度
     */
    private final ReplicatingState replicatingState;


    public GroupMember(NodeEndpoint endpoint) {
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
        Assert.isTrue(replicatingState == null, () -> new IllegalStateException("replication state not set"));
        return replicatingState;
    }

}
