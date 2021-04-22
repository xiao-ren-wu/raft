package org.ywb.raft.core.log.entry;

/**
 * @author yuwenbo1
 * @date 2021/4/22 9:49 下午 星期四
 * @since 1.0.0
 */
public interface Entry {
    /**
     * 日志类型
     */
    int KIND_NO_OP = 0;

    int KIND_GENERAL = 1;

    /**
     * 获取类型
     *
     * @return kind
     */
    int getKind();

    /**
     * 获取索引
     *
     * @return index
     */
    int getIndex();

    /**
     * 获取term
     *
     * @return term
     */
    int getTerm();

    /**
     * 获取元信息
     *
     * @return meta
     */
    EntryMeta getMeta();

    /**
     * 获取日志body
     *
     * @return body
     */
    byte[] getCommandBytes();
}
