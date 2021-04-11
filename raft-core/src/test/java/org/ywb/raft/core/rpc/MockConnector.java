package org.ywb.raft.core.rpc;

import lombok.Getter;
import lombok.ToString;
import org.ywb.raft.core.support.meta.NodeEndpoint;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.core.rpc.msg.AppendEntriesResult;
import org.ywb.raft.core.rpc.msg.AppendEntriesRpc;
import org.ywb.raft.core.rpc.msg.RequestVoteResult;
import org.ywb.raft.core.rpc.msg.RequestVoteRpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yuwenbo1
 * @date 2021/4/11 11:51 上午 星期日
 * @since 1.0.0
 */
public class MockConnector implements Connector {

    private LinkedList<Message> messages = new LinkedList<>();

    @Override
    public void initialize() {
    }

    @Override
    public void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints) {
        Message message = new Message();
        message.rpc = rpc;
        messages.add(message);
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint) {
        Message message = new Message();
        message.result = result;
        message.destinationNodeId = destinationEndpoint.getNodeId();
        messages.add(message);
    }

    @Override
    public void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint) {
        Message message = new Message();
        message.rpc = rpc;
        message.destinationNodeId = destinationEndpoint.getNodeId();
        messages.add(message);
    }

    @Override
    public void replyEntries(AppendEntriesResult result, NodeEndpoint destinationEndpoint) {
        Message message = new Message();
        message.result = result;
        message.destinationNodeId = destinationEndpoint.getNodeId();
        messages.add(message);
    }

    @Override
    public void close() {
    }


    public Message getLastMessage() {
        return messages.isEmpty() ? null : messages.getLast();
    }

    private Message getLastMessageOrDefault() {
        return messages.isEmpty() ? new Message() : messages.getLast();
    }

    public Object getRPC() {
        return getLastMessageOrDefault().rpc;
    }

    public Object getResult(){
        return getLastMessageOrDefault().result;
    }

    public NodeId getDestinationNodeId() {
        return getLastMessageOrDefault().destinationNodeId;
    }

    public int getMessageCount() {
        return messages.size();
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clearMessage(){
        messages.clear();
    }


    @Getter
    @ToString
    public static class Message {
        private Object rpc;
        private NodeId destinationNodeId;
        private Object result;
    }
}
