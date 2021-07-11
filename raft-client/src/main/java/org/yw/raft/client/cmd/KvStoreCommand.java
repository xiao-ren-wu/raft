package org.yw.raft.client.cmd;

import com.beust.jcommander.Parameter;
import lombok.Data;

import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/7/11 10:38 上午 星期日
 * @since 1.0.0
 */
@Data
public class KvStoreCommand {

    @Parameter(names = "get", description = "get value")
    private String get;

    @Parameter(names = "set", description = "create or update key-value")
    private List<String> set;
}
