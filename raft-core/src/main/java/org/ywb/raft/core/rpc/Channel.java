package org.ywb.raft.core.rpc;

import org.ywb.raft.core.rpc.msg.AppendEntriesResult;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;

import javax.annotation.Nonnull;

/**
 * @author yuwenbo1
 * @date 2021/5/11 8:04 上午 星期二
 * @since 1.0.0
 */
public interface Channel {
    /**
     * Write request vote rpc.
     *
     * @param rpc rpc
     */
    void writeRequestVoteRpc(@Nonnull RequestVoteRpc rpc);

    /**
     * Write request vote result.
     *
     * @param result result
     */
    void writeRequestVoteResult(@Nonnull RequestVoteResult result);

    /**
     * Write append entries rpc.
     *
     * @param rpc rpc
     */
    void writeAppendEntriesRpc(@Nonnull AppendEntriesRpc rpc);

    /**
     * Write append entries result.
     *
     * @param result result
     */
    void writeAppendEntriesResult(@Nonnull AppendEntriesResult result);

//    /**
//     * Write install snapshot rpc.
//     *
//     * @param rpc rpc
//     */
//    void writeInstallSnapshotRpc(@Nonnull InstallSnapshotRpc rpc);
//
//    /**
//     * Write install snapshot result.
//     *
//     * @param result result
//     */
//    void writeInstallSnapshotResult(@Nonnull InstallSnapshotResult result);

    /**
     * Close channel.
     */
    void close();

}
