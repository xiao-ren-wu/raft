package org.ywb.raft.kvstore.config;

import lombok.Data;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.utils.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class RaftConfig {

    private String nodeId;

    private Integer servicePort;

    private Map<String, String> groupMember;

    private String dataDir;

    private String mode;

    public Set<NodeEndpoint> nodeEndpoints;

    public Set<NodeEndpoint> getNodeEndpoints() {
        HashSet<NodeEndpoint> nodeEndpoints = new HashSet<>();
        groupMember.forEach((k, v) -> {
            Assert.hasText(k, "nodeId must not be null");
            String[] split = v.split(":");
            Assert.isTrue(split.length == 2, () -> new IllegalArgumentException("nodeEndpoint config error! =>" + v));
            NodeEndpoint nodeEndpoint = new NodeEndpoint(k, split[0], Integer.parseInt(split[1]));
            nodeEndpoints.add(nodeEndpoint);
        });
        return nodeEndpoints;
    }
}