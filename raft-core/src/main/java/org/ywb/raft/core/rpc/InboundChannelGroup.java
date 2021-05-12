package org.ywb.raft.core.rpc;

import lombok.extern.slf4j.Slf4j;
import org.ywb.raft.core.support.meta.NodeId;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yuwenbo1
 * @date 2021/5/12 8:06 上午 星期三
 * @since 1.0.0
 */
@Slf4j
public class InboundChannelGroup {

    private final List<NioChannel> channels = new CopyOnWriteArrayList<>();

    public void add(NodeId remoteId, NioChannel channel) {
        log.debug("channel INBOUND-{} connected", remoteId);
        channel.getDelegate()
                .closeFuture()
                .addListener(cf -> {
                    log.debug("channel INBOUND-{} disconnected", remoteId);
                    remove(channel);
                });
    }

    private void remove(NioChannel channel) {
        channels.remove(channel);
    }

    public void closeAll() {
        log.debug("close all inbound channels");
        channels.forEach(NioChannel::close);
    }

}
