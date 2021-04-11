package org.ywb.raft.core.support.meta;

import com.google.common.base.Preconditions;
import org.ywb.raft.core.utils.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:32 下午 星期一
 * @since 1.0.0
 * 节点群组信息
 */
public class NodeGroup {

    /**
     * 当前节点的nodeId
     */
    private final NodeId selfNodeId;

    /**
     * 群组成员列表
     */
    private final Map<NodeId, GroupMember> memberMap;

    public NodeGroup(NodeId selfNodeId, Collection<NodeEndpoint> endpoints) {
        this.selfNodeId = selfNodeId;
        this.memberMap = buildMemberMap(endpoints);
    }

    private Map<NodeId, GroupMember> buildMemberMap(Collection<NodeEndpoint> endpoints) {
        Map<NodeId, GroupMember> groupMemberMap =
                endpoints.stream()
                        .collect(Collectors.toMap(NodeEndpoint::getNodeId, GroupMember::new));
        Assert.isFalse(groupMemberMap.isEmpty(), () -> new IllegalArgumentException("endpoint must not be empty."));
        return groupMemberMap;
    }

    public GroupMember findGroupMember(NodeId nodeId) {
        GroupMember groupMember = memberMap.get(nodeId);
        return Preconditions.checkNotNull(groupMember, "no such node: " + nodeId);
    }

    /**
     * 获取除自己之外的群组中的成员列表
     *
     * @return list of other node in this group
     */
    public Collection<GroupMember> listReplicationTarget() {
        return memberMap.values()
                .stream()
                .filter(a -> !a.getEndpoint().getNodeId().equals(this.selfNodeId))
                .collect(Collectors.toList());
    }

    /**
     * 获取除自己之外集群列表成员的endpoint
     *
     * @return list of other node endpoint in this group
     */
    public Collection<NodeEndpoint> listEndpointExceptSelf() {
        return memberMap.values()
                .stream()
                .map(GroupMember::getEndpoint)
                .filter(nodeEndpoint -> !nodeEndpoint.getNodeId().equals(this.selfNodeId))
                .collect(Collectors.toList());
    }

    public int getCount(){
        return memberMap.size();
    }
}
