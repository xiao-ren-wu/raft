package org.yw.raft.client.message;

import lombok.Getter;
import lombok.ToString;

/**
 * @author yuwenbo1
 * @date 2021/5/17 9:52 下午 星期一
 * @since 1.0.0
 */
@ToString
public class GetCommandResponse {

    private final boolean found;

    @Getter
    private final byte[] value;

    public GetCommandResponse(boolean found, byte[] value) {
        this.found = found;
        this.value = value;
    }

    public GetCommandResponse(byte[] value) {
        this.found = value!=null;
        this.value = value;
    }

    public boolean isFound(){
        return found;
    }
}
