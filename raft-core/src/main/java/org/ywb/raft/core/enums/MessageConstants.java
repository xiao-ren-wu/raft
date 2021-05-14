package org.ywb.raft.core.enums;

/**
 * @author yuwenbo1
 * @date 2021/5/10 10:26 下午 星期一
 * @since 1.0.0
 */
public interface MessageConstants {

    int MSG_TYPE_NODE_ID = 0;

    int MSG_TYPE_REQUEST_VOTE_RPC = 1;
    int MSG_TYPE_REQUEST_VOTE_RESULT = 2;

     int MAGIC = 0xBCDE;

     int VERSION = 1;

     int HEADER_LEN = 16;

}
