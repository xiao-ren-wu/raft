package org.ywb.raft.core.support.meta;

import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:09 下午 星期一
 * @since 1.0.0
 * 节点信息
 */
@Data
public class NodeId {

    private final String val;

    public NodeId(String val) {
        Preconditions.checkNotNull(val);
        this.val = val;
    }

    public static NodeId of(String val) {
        return new NodeId(val);
    }

}
