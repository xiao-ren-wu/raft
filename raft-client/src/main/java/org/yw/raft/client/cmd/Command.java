package org.yw.raft.client.cmd;

import org.yw.raft.client.support.CommandContext;

/**
 * @author yuwenbo1
 * @date 2021/7/11 11:31 上午 星期日
 * @since 1.0.0
 */
public interface Command {
    /**
     * 获取命令名称
     *
     * @return get
     */
    String getName();

    /**
     * 执行命令
     *
     * @param args    参数
     * @param context 命令上下文
     */
    void execute(String args, CommandContext context);
}
