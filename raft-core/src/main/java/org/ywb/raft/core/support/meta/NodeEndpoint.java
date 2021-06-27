package org.ywb.raft.core.support.meta;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:09 下午 星期一
 * @since 1.0.0
 * 节点元数据
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class NodeEndpoint {

    private final NodeId nodeId;

    private final Address address;

    public NodeEndpoint(String id, String host, int port) {
        this.nodeId = NodeId.of(id);
        this.address = new Address(host, port);
    }

}
