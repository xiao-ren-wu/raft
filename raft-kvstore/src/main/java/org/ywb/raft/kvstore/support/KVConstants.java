package org.ywb.raft.kvstore.support;

/**
 * @author yuwenbo1
 * @date 2021/6/27 9:57 下午 星期日
 * @since 1.0.0
 */
public interface KVConstants {

    interface RAFT_MODE {
        String STANDALONE = "standalone";
        String STANDBY = "standby";
        String MODE_GROUP_MEMBER = "group-member";
    }

}
