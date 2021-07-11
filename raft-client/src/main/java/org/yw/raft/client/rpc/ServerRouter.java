package org.yw.raft.client.rpc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yw.raft.client.exceptions.NoAvailableServerException;
import org.yw.raft.client.exceptions.RedirectException;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuwenbo1
 * @date 2021/7/11 10:51 上午 星期日
 * @since 1.0.0
 */
@Slf4j
@Data
public class ServerRouter {

    private final Map<NodeId, KvChannel> availableServer = new HashMap<>();

    private NodeId leaderId;

    public Object send(Object payload) {
        for (NodeId nodeId : getCandidateNodeIds()) {
            try {
                Object result = doSend(nodeId, payload);
                this.leaderId = nodeId;
                return result;
            } catch (RedirectException e) {
                log.debug("not a leader server,redirect to server {}", e.getLeaderId());
                this.leaderId = e.getLeaderId();
                return doSend(e.getLeaderId(), payload);
            } catch (Exception e) {
                log.debug("failed to process with server {},cause {}", nodeId, e.getMessage());
            }
        }
        throw new NoAvailableServerException();
    }

    private Object doSend(NodeId leaderId, Object payload) {
        KvChannel kvChannel = availableServer.get(leaderId);
        return kvChannel.send(payload);
    }

    private Collection<NodeId> getCandidateNodeIds() {
        if (availableServer.isEmpty()) {
            throw new NoAvailableServerException();
        }
        if (leaderId != null) {
            List<NodeId> nodeIdList = availableServer.keySet()
                    .stream()
                    .filter(nodeId -> !nodeId.equals(leaderId))
                    .collect(Collectors.toList());
            nodeIdList.add(0, leaderId);
            return nodeIdList;
        }
        return availableServer.keySet();
    }

    public void add(NodeId nodeId, KvChannel kvChannel) {
        availableServer.put(nodeId, kvChannel);
    }
}
