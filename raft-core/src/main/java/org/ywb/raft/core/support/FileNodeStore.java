//package org.ywb.raft.core.support;
//
//import org.ywb.raft.core.support.meta.NodeId;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//
///**
// * @author yuwenbo1
// * @date 2021/4/7 11:55 下午 星期三
// * @since 1.0.0
// * 基于文件的形式存储
// */
//public class FileNodeStore implements NodeStore {
//
//    private static final String FILE_NAME = "node.bin";
//
//    private static final long OFFSET_TERM = 0;
//
//    private static final long OFFSET_VOTED_FOR = 4;
//
//    private final RandomAccessFile seekableFile;
//
//    private int term;
//
//    private NodeId votedFor;
//
//    public FileNodeStore(File file) {
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            seekableFile = new RandomAccessFile(file, "rw");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public int getTerm() {
//        return 0;
//    }
//
//    @Override
//    public void setTerm(int term) {
//
//    }
//
//    @Override
//    public NodeId getVotedFor() {
//        return null;
//    }
//
//    @Override
//    public void setVotedFor(NodeId nodeId) {
//
//    }
//
//    @Override
//    public void close() {
//
//    }
//
//    private void initializeOrLoad() throws IOException {
//        if(seekableFile.)
//    }
//}
