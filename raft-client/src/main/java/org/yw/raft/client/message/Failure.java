package org.yw.raft.client.message;

import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/5/17 9:54 下午 星期一
 * @since 1.0.0
 */
@Getter
@ToString
public class Failure {

    private final int  errCode;

    private final String message;

    public Failure(int errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }


}
