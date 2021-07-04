package org.ywb.codec.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yuwenbo1
 * @date 2021/7/3 4:03 下午 星期六
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class Message {

    private MessageHeader header;

    private byte[] payload;
}
