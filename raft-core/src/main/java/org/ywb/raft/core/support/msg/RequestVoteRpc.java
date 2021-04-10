package org.ywb.raft.core.support.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/4/6 11:23 下午 星期二
 * @since 1.0.0
 * 请求投票消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestVoteRpc {

    /**
     * 选举term
     */
    private int term;

    /**
     * 跟随者节点id，一般都是发送者自己
     */
    private NodeId candidateId;

    /**
     * 候选值最后一条日志索引
     */
    private int lastLogIndex = 0;

    /**
     * 候选者最后一条日志term
     */
    private int lastLogTerm = 0;
}
