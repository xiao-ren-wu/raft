package org.ywb.raft.core.support;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:18 下午 星期一
 * @since 1.0.0
 * 日志复制记录
 */
public interface ReplicatingState {
    /**
     * 获取nextIndex
     *
     * @return next index
     */
    int getNextIndex();

    /**
     * 获取matchIndex
     *
     * @return match index
     */
    int getMatchIndex();
}
