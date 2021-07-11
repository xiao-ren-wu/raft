package org.yw.raft.client.rpc;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.yw.raft.client.exceptions.ChannelException;
import org.yw.raft.client.exceptions.RedirectException;
import org.yw.raft.client.message.GetCommand;
import org.yw.raft.client.message.SetCommand;
import org.ywb.codec.ProtocolUtils;
import org.ywb.codec.consts.MessageConstants;
import org.ywb.codec.protocol.Message;
import org.ywb.codec.protocol.MessageHeader;
import org.ywb.raft.core.exceptions.ChannelConnectException;
import org.ywb.raft.core.support.meta.NodeId;
import org.ywb.raft.kvstore.Protos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author yuwenbo1
 * @date 2021/7/3 3:26 下午 星期六
 * @since 1.0.0
 */
@Slf4j
public class KvChannel {

    private final String host;

    private final int port;

    public KvChannel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Object send(Object payload) {
        try (Socket socket = new Socket()) {
            socket.setTcpNoDelay(true);
            log.debug("connect {},{}", host, port);
            socket.connect(new InetSocketAddress(this.host, this.port));
            log.debug("connect success...");
            this.write(socket.getOutputStream(), payload);
            log.debug("finish request...wait response");
            Object response = this.read(socket.getInputStream());
            log.debug("response msg,{}", response);
            return response;
        } catch (IOException e) {
            throw new ChannelConnectException("failed to send and receive", e);
        }
    }

    private Object read(InputStream in) throws IOException {
        Message message = ProtocolUtils.read(in);
        MessageHeader header = message.getHeader();
        int messageType = header.getMessageType();
        byte[] payload = message.getPayload();
        switch (messageType) {
            case MessageConstants.MSG_TYPE_SUCCESS:
                return null;
            case MessageConstants.MSG_TYPE_FAILURE:
                Protos.Failure protoFailure = Protos.Failure.parseFrom(payload);
                throw new ChannelException("error code " + protoFailure.getErrorCode() + ", message " + protoFailure.getMessage());
            case MessageConstants.MSG_TYPE_REDIRECT:
                Protos.Redirect protoRedirect = Protos.Redirect.parseFrom(payload);
                throw new RedirectException(new NodeId(protoRedirect.getLeaderId()));
            case MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE:
                Protos.GetCommandResponse protoGetCommandResponse = Protos.GetCommandResponse.parseFrom(payload);
                if (!protoGetCommandResponse.getFound()) {
                    return null;
                }
                return protoGetCommandResponse.getValue().toByteArray();
            default:
                throw new ChannelException("unexpected message type " + messageType);
        }
    }

    private void write(OutputStream out, Object payload) throws IOException {
        if (payload instanceof GetCommand) {
            Protos.GetCommand protoGetCommand = Protos.GetCommand.newBuilder().setKey(((GetCommand) payload).getKey()).build();
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_GET_COMMAND, protoGetCommand.toByteArray());
        } else if (payload instanceof SetCommand) {
            SetCommand setCommand = (SetCommand) payload;
            Protos.SetCommand protoSetCommand = Protos.SetCommand.newBuilder()
                    .setKey(setCommand.getKey())
                    .setValue(ByteString.copyFrom(setCommand.getValue())).build();
            ProtocolUtils.write(out, MessageConstants.MSG_TYPE_SET_COMMAND, protoSetCommand.toByteArray());
        }
        // todo
        /*else if (payload instanceof AddNodeCommand) {
            AddNodeCommand command = (AddNodeCommand) payload;
            Protos.AddNodeCommand protoAddServerCommand = Protos.AddNodeCommand.newBuilder().setNodeId(command.getNodeId())
                    .setHost(command.getHost()).setPort(command.getPort()).build();
            ProtocolUtils.write(output, MessageConstants.MSG_TYPE_ADD_SERVER_COMMAND, protoAddServerCommand);
        } else if (payload instanceof RemoveNodeCommand) {
            RemoveNodeCommand command = (RemoveNodeCommand) payload;
            Protos.RemoveNodeCommand protoRemoveServerCommand = Protos.RemoveNodeCommand.newBuilder().setNodeId(command.getNodeId().getValue()).build();
            ProtocolUtils.write(output, MessageConstants.MSG_TYPE_REMOVE_SERVER_COMMAND, protoRemoveServerCommand);
        }*/
    }
}
