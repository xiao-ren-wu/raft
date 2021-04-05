package org.ywb.raft.core.support.meta;

import com.google.common.base.Preconditions;
import lombok.Getter;

/**
 * @author yuwenbo1
 * @date 2021/4/5 6:05 下午 星期一
 * @since 1.0.0
 * 用于记录每个节点的IP和PORT
 */
@Getter
public class Address {

    private final String host;

    private final int port;

    public Address(String host, int port) {
        Preconditions.checkNotNull(host);
        this.host = host;
        this.port = port;
    }
}
