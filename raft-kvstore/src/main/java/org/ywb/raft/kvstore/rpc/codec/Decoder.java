package org.ywb.raft.kvstore.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.ywb.codec.ProtocolUtils;
import org.ywb.codec.protocol.Message;
import org.ywb.raft.core.enums.MessageConstants;
import org.ywb.raft.kvstore.Protos;
import org.ywb.raft.kvstore.message.*;

import java.util.List;
import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/6/1 10:22 下午 星期二
 * @since 1.0.0
 */
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        Message message = ProtocolUtils.read(in);
        if (Objects.isNull(message)) {
            return;
        }
        int msgType = message.getHeader().getMessageType();
        byte[] payload = message.getPayload();
        switch (msgType) {
            case MessageConstants.MSG_TYPE_SUCCESS:
                out.add(Success.INSTANCE);
                break;
            case MessageConstants.MSG_TYPE_FAILURE:
                Protos.Failure protoFailure = Protos.Failure.parseFrom(payload);
                out.add(new Failure(protoFailure.getErrorCode(), protoFailure.getMessage()));
                break;
            case MessageConstants.MSG_TYPE_REDIRECT:
                Protos.Redirect protoDirect = Protos.Redirect.parseFrom(payload);
                out.add(new Redirect(protoDirect.getLeaderId()));
                break;
            case MessageConstants.MSG_TYPE_GET_COMMAND:
                Protos.GetCommand getCommand = Protos.GetCommand.parseFrom(payload);
                out.add(new GetCommand(getCommand.getKey()));
                break;
            case MessageConstants.MSG_TYPE_GET_COMMAND_RESPONSE:
                Protos.GetCommandResponse getCommandResponse = Protos.GetCommandResponse.parseFrom(payload);
                out.add(new GetCommandResponse(getCommandResponse.getFound(), getCommandResponse.getValue().toByteArray()));
                break;
            case MessageConstants.MSG_TYPE_SET_COMMAND:
                Protos.SetCommand setCommand = Protos.SetCommand.parseFrom(payload);
                out.add(new SetCommand(setCommand.getKey(), setCommand.getValue().toByteArray()));
                break;
            default:
                throw new IllegalArgumentException("unexpected message type " + msgType);
        }
    }
}
