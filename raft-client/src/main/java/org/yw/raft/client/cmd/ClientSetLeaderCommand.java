package org.yw.raft.client.cmd;

import com.google.common.base.Strings;
import org.yw.raft.client.support.CommandContext;
import org.ywb.raft.core.support.meta.NodeId;

/**
 * @author yuwenbo1
 * @date 2021/7/11 11:34 上午 星期日
 * @since 1.0.0
 */
public class ClientSetLeaderCommand implements Command {

    @Override
    public String getName() {
        return "set-leader";
    }

    @Override
    public void execute(String args, CommandContext context) {
        if (Strings.isNullOrEmpty(args)) {
            throw new IllegalArgumentException("usage: " + getName() + "<node-id>");
        }
        NodeId nodeId = NodeId.of(args);
        try {
            context.setLeaderId(nodeId);
            System.out.println(nodeId);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
