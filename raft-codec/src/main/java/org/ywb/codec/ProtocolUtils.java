package org.ywb.codec;

import io.netty.buffer.ByteBuf;
import org.ywb.codec.protocol.Message;
import org.ywb.codec.protocol.MessageHeader;
import org.ywb.codec.support.MagicCodeErrorException;

import java.io.*;

import static org.ywb.codec.consts.ProtocolConstant.*;

/**
 * @author yuwenbo1
 * @date 2021/7/3 4:08 下午 星期六
 * @since 1.0.0
 */
public final class ProtocolUtils {

    private ProtocolUtils() {
    }

    /**
     * 读取byteBuf中的内容，如果byteBuf没有准备好，返回空消息
     *
     * @param byteBuf byteByf
     * @return Message
     */
    public static Message read(ByteBuf byteBuf) {
        int availableByte = byteBuf.readableBytes();
        byteBuf.markReaderIndex();
        // 是否多余16个字节可读，16字节为自定义协议header的长度
        if (availableByte < HEADER_LEN) {
            return null;
        }
        // generate header
        MessageHeader header = new MessageHeader();
        int magic = byteBuf.readInt();
        if (RAFT_MAGIC - magic != 0) {
            throw new MagicCodeErrorException(magic);
        }
        // version
        header.setVersion(byteBuf.readInt());
        // msg type
        header.setMessageType(byteBuf.readInt());
        // payload len
        int payloadLen = byteBuf.readInt();
        header.setPayloadLen(payloadLen);
        if (byteBuf.readableBytes() < payloadLen) {
            // 消息未完全可读，回到起始位置
            byteBuf.resetReaderIndex();
            return null;
        }
        // 消息可读
        byte[] payload = new byte[payloadLen];
        byteBuf.readBytes(payload);
        return new Message(header, payload);
    }

    public static void write(ByteBuf out, int msgType, byte[] bytes) {
        out.writeInt(RAFT_MAGIC);
        out.writeInt(VERSION);
        out.writeInt(msgType);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    public static Message read(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);
        MessageHeader header = new MessageHeader();
        int magic = dataInput.readInt();
        if (RAFT_MAGIC - magic != 0) {
            throw new MagicCodeErrorException(magic);
        }
        // version
        header.setVersion(dataInput.readInt());
        // msg type
        header.setMessageType(dataInput.readInt());
        // payload len
        int payloadLen = dataInput.readInt();
        header.setPayloadLen(payloadLen);
        int messageType = dataInput.readInt();
        header.setMessageType(messageType);
        int payloadLength = dataInput.readInt();
        byte[] payload = new byte[payloadLength];
        dataInput.readFully(payload);
        return new Message(header, payload);
    }

    public static void write(OutputStream output, int messageType, byte[] payload) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(RAFT_MAGIC);
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(messageType);
        dataOutput.writeInt(payload.length);
        dataOutput.write(payload);
        dataOutput.flush();
    }
}
