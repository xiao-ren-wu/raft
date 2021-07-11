package org.ywb.raft.kvstore.support;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.enums.RoleName;
import org.ywb.raft.core.node.Node;
import org.ywb.raft.core.node.support.RoleNameAndLeaderId;
import org.ywb.raft.core.statemachine.AbstractSingleThreadStateMachine;
import org.ywb.raft.kvstore.message.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuwenbo1
 * @date 2021/5/31 10:12 下午 星期一
 * @since 1.0.0
 */
@Slf4j
public class Service {

    private final Node node;

    /**
     * 用于做请求和workId的映射
     */
    private final ConcurrentHashMap<String, CommandRequest<?>> pendingCommands = new ConcurrentHashMap<>();

    /**
     * K-V服务的数据
     */
    private final Map<String, byte[]> map = new HashMap<>();

    public Service(Node node) {
        this.node = node;
        node.registerStateMachine(new StateMachineImpl());
    }

    public void set(CommandRequest<SetCommand> commandRequest) {
        Redirect redirect = checkLeadership();
        // todo 选举中，选中的节点为Candidate时，服务不可用!!!
        if (redirect != null) {
            commandRequest.reply(redirect);
            return;
        }
        SetCommand command = commandRequest.getCommand();
        log.debug("set {}-{}", command.getKey(),command.getValue());
        // 记录请求ID和CommandRequest的映射
        this.pendingCommands.put(command.getRequestId(), commandRequest);
        // 客户端关闭连接时从映射中移除
        commandRequest.addCloseListener(() -> pendingCommands.remove(command.getRequestId()));
        // 追加日志
        this.node.appendLog(command.toBytes());
    }

    public void get(CommandRequest<GetCommand> commandRequest){
        String key = commandRequest.getCommand().getKey();
        log.debug("get {}",key);
        // TODO 日志复制之后才能进行get
        byte[] value = this.map.get(key);
        commandRequest.reply(new GetCommandResponse(value));
    }

    private Redirect checkLeadership() {
        RoleNameAndLeaderId state = node.getRoleNameAndLeaderId();
        if (state.getRoleName() != RoleName.LEADER) {
            return new Redirect(state.getLeaderNodeId());
        }
        return null;
    }

    private class StateMachineImpl extends AbstractSingleThreadStateMachine {

        @Override
        protected void applyCommand(byte[] commandBytes) {
            // 恢复命令
            SetCommand setCommand = SetCommand.fromBytes(commandBytes);
            // 修改数据
            map.put(setCommand.getKey(), setCommand.getValue());
            // 查找连接
            CommandRequest<?> commandRequest = pendingCommands.remove(setCommand.getRequestId());
            if (commandRequest != null) {
                commandRequest.reply(Success.INSTANCE);
            }
        }
    }
}
