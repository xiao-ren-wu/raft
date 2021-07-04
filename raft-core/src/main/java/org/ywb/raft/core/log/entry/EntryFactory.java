package org.ywb.raft.core.log.entry;

import com.google.protobuf.ByteString;
import org.ywb.raft.core.proto.Protos;

/**
 * @author yuwenbo1
 * @date 2021/4/25 10:14 下午 星期日
 * @since 1.0.0
 */
public class EntryFactory {

    public static Entry create(int kind, int index, int term, byte[] commandBytes) {
        switch (kind) {
            case Entry.KIND_GENERAL:
                return new GeneralEntry(index, term, commandBytes);
            case Entry.KIND_NO_OP:
                return new NoOpEntry(index, term);
            default:
                throw new IllegalArgumentException(String.format("kind %s not found", kind));
        }
    }

    public static Entry proto2Entry(Protos.AppendEntriesRpc.Entry entry) {
        return create(entry.getKind(), entry.getIndex(), entry.getTerm(), entry.getData().toByteArray());
    }

    public static Protos.AppendEntriesRpc.Entry entry2Proto(Entry entry) {
        return Protos.AppendEntriesRpc.Entry.newBuilder()
                .setTerm(entry.getTerm())
                .setKind(entry.getKind())
                .setIndex(entry.getIndex())
                .setData(ByteString.copyFrom(entry.getCommandBytes()))
                .build();
    }
}
