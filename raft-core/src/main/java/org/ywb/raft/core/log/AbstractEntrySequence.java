package org.ywb.raft.core.log;

/**
 * @author yuwenbo1
 * @date 2021/4/22 10:26 下午 星期四
 * @since 1.0.0
 *                            lastLogIndex
 *                             |
 * +-------+-------+-------+-------+- - - -+
 * |   E   |   E   |   E   |   E   |   E   |
 * +-------+-------+-------+-------+- - - -+
 *   |                                |
 *   logIndexOffset                 nextLogIndex
 */
public abstract class AbstractEntrySequence implements EntrySequence {

    int logIndexOffset;

    int nextLogIndex;


}
