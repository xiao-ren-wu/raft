package org.ywb.raft.kvstore.message;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.ToString;
import org.ywb.raft.kvstore.Protos;

import java.util.UUID;

/**
 * @author yuwenbo1
 * @date 2021/5/17 9:57 下午 星期一
 * @since 1.0.0
 */
@Getter
@ToString
public class SetCommand {

    private final String requestId;

    private final String key;

    private final byte[] value;

    public SetCommand(String key, byte[] value) {
        this.requestId = UUID.randomUUID().toString();
        this.key = key;
        this.value = value;
    }

    public SetCommand(String requestId, String key, byte[] value) {
        this.requestId = requestId;
        this.key = key;
        this.value = value;
    }

    public static SetCommand fromBytes(byte[] bytes) {
        try {
            Protos.SetCommand protoCommand = Protos.SetCommand.parseFrom(bytes);
            return new SetCommand(
                    protoCommand.getRequestId(),
                    protoCommand.getKey(),
                    protoCommand.getValue().toByteArray()
            );
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException("failed to deserialize set command ", e);
        }
    }

    public byte[] toBytes() {
        return Protos.SetCommand.newBuilder()
                .setRequestId(this.requestId)
                .setKey(this.key)
                .setValue(ByteString.copyFrom(this.value))
                .build()
                .toByteArray();
    }
}
