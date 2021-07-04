package org.ywb.raft.core.support.meta;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.support.ReplicatingState;
import org.ywb.raft.core.utils.Assert;
import sun.rmi.transport.Channel;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:32 下午 星期一
 * @since 1.0.0
 * 节点群组信息
 */
@Slf4j
@ToString
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

    public int getCount() {
        return memberMap.size();
    }

    public int getMatchIndexOfMajor() {
        List<NodeMatchIndex> matchIndices = new ArrayList<>();
        for (GroupMember member : memberMap.values()) {
            if (!member.idEquals(selfNodeId)) {
                matchIndices.add(new NodeMatchIndex(member.getId(), member.getMatchIndex()));
            }
        }
        int count = matchIndices.size();
        // 没有节点的情况
        if (count == 0) {
            throw new IllegalStateException("standalone or no major node");
        }
        Collections.sort(matchIndices);
        log.debug("match indices {}", matchIndices);
        // 取排序后中间位置的matchIndex
        return matchIndices.get(count / 2).getMatchIndex();
    }

    public GroupMember findSelf() {
        return findGroupMember(selfNodeId);
    }

    public void resetReplicatingStates(int idx) {
        for (GroupMember member : memberMap.values()) {
            if (!member.idEquals(selfNodeId)) {
                member.setReplicatingState(new ReplicatingState(idx));
            }
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    private static class NodeMatchIndex implements Comparable<NodeMatchIndex> {

        private final NodeId nodeId;

        private final int matchIndex;

        @Override
        public int compareTo(@Nonnull NodeMatchIndex o) {
            return Integer.compare(this.matchIndex, o.matchIndex);
        }
    }
}
