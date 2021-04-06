package org.ywb.raft.core.enums;

/**
 * @author yuwenbo1
 * @date 2021/4/6 10:44 下午 星期二
 * @since 1.0.0
 * raft中的角色列表
 */
public enum RoleName {
    /**
     * 跟随者
     */
    FOLLOWER,
    /**
     * 选举者
     */
    CANDIDATE,
    /**
     * 领导者
     */
    LEADER
}
