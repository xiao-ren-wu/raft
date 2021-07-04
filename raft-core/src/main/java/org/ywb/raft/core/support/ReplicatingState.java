package org.ywb.raft.core.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:18 下午 星期一
 * @since 1.0.0
 * 日志复制记录
 */
@Getter
@Setter
@ToString
@Slf4j
public class ReplicatingState {

    private int nextIndex;
    private int matchIndex;
    private boolean replicating = false;
    private long lastReplicatedAt = 0;

    public ReplicatingState(int nextIndex) {
        this(nextIndex, 0);
    }

    public ReplicatingState(int nextIndex, int matchIndex) {
        this.nextIndex = nextIndex;
        this.matchIndex = matchIndex;
    }

    public boolean advance(int lastEntryIndex) {
        log.debug("matchIndex={},lastEntryIndex={},nextIndex={}",matchIndex,lastEntryIndex,nextIndex);
        // todo changed,我认为是==
        boolean result = (matchIndex == lastEntryIndex || nextIndex == (lastEntryIndex + 1));

        matchIndex = lastEntryIndex;
        nextIndex = lastEntryIndex + 1;

        return result;
    }

    public boolean backOffNextIndex() {
        if (nextIndex > 1) {
            nextIndex--;
            return true;
        }
        return false;
    }



}
