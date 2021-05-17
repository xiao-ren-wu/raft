package org.ywb.raft.kvstore.message;

import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/5/17 9:51 下午 星期一
 * @since 1.0.0
 */
@ToString
@Getter
public class GetCommand {

    private final String key;

    public GetCommand(String key) {
        this.key = key;
    }
}
