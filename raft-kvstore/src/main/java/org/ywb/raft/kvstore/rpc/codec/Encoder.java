package org.ywb.raft.kvstore.rpc.codec;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.ywb.raft.core.enums.MessageConstants;
import org.ywb.raft.kvstore.Protos;
import org.ywb.raft.kvstore.message.*;

import static org.ywb.raft.core.enums.MessageConstants.*;

/**
 * @author yuwenbo1
 * @date 2021/6/1 7:58 上午 星期二
 * @since 1.0.0
 * +----------------+----------+-----+---------+
 * |  magic(0xBCDE) |  version | type| bodyLen |
 * +----------------+----------+-----+---------+
 * |               PAYLOAD                     |
 * +-------------------------------------------+
 */
public class Encoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {
        if (o instanceof Success) {
            this.writeMessage(MSG_TYPE_SUCCESS, Protos.Success.newBuilder().build(), out);
        } else if (o instanceof Failure) {
            Failure failure = (Failure) o;
            this.writeMessage(MSG_TYPE_FAILURE,
                    Protos.Failure
                            .newBuilder()
                            .setErrorCode(failure.getErrCode())
                            .setMessage(failure.getMessage())
                            .build(), out);
        } else if (o instanceof Redirect) {
            Redirect redirect = (Redirect) o;
            this.writeMessage(MSG_TYPE_REDIRECT,
                    Protos.Redirect
                            .newBuilder()
                            .setLeaderId(redirect.getLeaderId())
                            .build(), out);
        } else if (o instanceof GetCommand) {
            GetCommand getCommand = (GetCommand) o;
            this.writeMessage(MSG_TYPE_GET_COMMAND,
                    Protos.GetCommand
                            .newBuilder()
                            .setKey(getCommand.getKey())
                            .build(), out);
        } else if (o instanceof GetCommandResponse) {
            GetCommandResponse getCommandResponse = (GetCommandResponse) o;
            byte[] value = getCommandResponse.getValue();
            Protos.GetCommandResponse protoResponse =
                    Protos.GetCommandResponse
                            .newBuilder()
                            .setFound(getCommandResponse.isFound())
                            .setValue(value != null ? ByteString.copyFrom(value) : ByteString.EMPTY)
                            .build();
            this.writeMessage(MSG_TYPE_GET_COMMAND_RESPONSE, protoResponse, out);
        } else if (o instanceof SetCommand) {
            SetCommand setCommand = (SetCommand) o;
            this.writeMessage(MessageConstants.MSG_TYPE_SET_COMMAND,
                    Protos.SetCommand
                            .newBuilder()
                            .setKey(setCommand.getKey())
                            .setValue(ByteString.copyFrom(setCommand.getValue()))
                            .build(), out);
        }
    }

    private void writeMessage(int msgType, MessageLite message, ByteBuf out) {
        byte[] bytes = message.toByteArray();
        out.writeInt(MessageConstants.KV_MAGIC);
        out.writeInt(MessageConstants.VERSION);
        out.writeInt(msgType);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
