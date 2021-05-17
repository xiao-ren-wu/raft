package org.ywb.raft.kvstore.message;

import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/5/17 9:56 下午 星期一
 * @since 1.0.0
 */
@Getter
@ToString
public class Redirect {

    private final String leaderId;

    public Redirect(String leaderId) {
        this.leaderId = leaderId;
    }

}
