package org.ywb.raft.core.enums;

/**
 * @author yuwenbo1
 * @date 2021/5/10 10:26 下午 星期一
 * @since 1.0.0
 */
public interface MessageConstants {

    int MSG_TYPE_NODE_ID = 0;

    int RAFT_MAGIC = 0xBCDE;
    int KV_MAGIC = 0x12BA;
    int VERSION = 1;
    int HEADER_LEN = 16;

    int MSG_TYPE_REQUEST_VOTE_RPC = 1;
    int MSG_TYPE_REQUEST_VOTE_RESULT = 2;

    int MSG_TYPE_SUCCESS = 3;
    int MSG_TYPE_FAILURE = 4;
    int MSG_TYPE_REDIRECT = 5;
    int MSG_TYPE_GET_COMMAND = 6;
    int MSG_TYPE_GET_COMMAND_RESPONSE = 7;
    int MSG_TYPE_SET_COMMAND = 8;
}
