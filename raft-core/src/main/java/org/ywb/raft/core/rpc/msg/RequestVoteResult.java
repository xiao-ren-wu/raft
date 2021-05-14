package org.ywb.raft.core.rpc.msg;

import lombok.Builder;

/**
 * @author yuwenbo1
 * @date 2021/4/6 11:26 下午 星期二
 * @since 1.0.0
 */
@Builder
public class RequestVoteResult {

    /**
     * 选举term
     */
    private final int term;

    /**
     * 是否投票
     */
    private final boolean voteGranted;

    public RequestVoteResult(int term, boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    /**
     * 获取term
     *
     * @return int of term
     */
    public int getTerm() {
        return this.term;
    }

    /**
     * 获取是否投票
     *
     * @return bool
     */
    public boolean isVoteGranted() {
        return this.voteGranted;
    }

    @Override
    public String toString() {
        return "RequestVoteResult{" +
                "term=" + term +
                ", voteGranted=" + voteGranted +
                '}';
    }
}
