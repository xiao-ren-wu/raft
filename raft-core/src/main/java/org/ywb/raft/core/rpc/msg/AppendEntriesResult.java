package org.ywb.raft.core.rpc.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/4/6 11:34 下午 星期二
 * @since 1.0.0
 */
@ToString
@Getter
@Builder
@AllArgsConstructor
public class AppendEntriesResult {

    /**
     * 选举term
     */
    private final int term;

    /**
     * 是否追加成功
     */
    private final boolean success;
}
