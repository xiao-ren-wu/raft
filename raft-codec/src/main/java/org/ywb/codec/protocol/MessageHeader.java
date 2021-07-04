package org.ywb.codec.protocol;

import lombok.Data;

/**
 * @author yuwenbo1
 * @date 2021/7/3 3:54 下午 星期六
 * @since 1.0.0
 * 协议定义
 * +----------------+----------+-----+------------+
 * |  magic(0xBCDE) |  version | type| payloadLen |
 * +----------------+----------+-----+------------+
 * |               PAYLOAD                        |
 * +----------------------------------------------+
 */
@Data
public class MessageHeader {

    private int magic;

    private int version;

    private int messageType;

    private int payloadLen;
}
