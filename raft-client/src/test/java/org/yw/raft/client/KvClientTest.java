package org.yw.raft.client;

import org.junit.Before;
import org.junit.Test;
import org.yw.raft.client.rpc.KvChannel;
import org.yw.raft.client.rpc.ServerRouter;
import org.ywb.raft.core.support.meta.NodeId;

import java.nio.charset.StandardCharsets;

public class KvClientTest {

    private KvClient kvClient;

    @Before
    public void before() {
        ServerRouter serverRouter = new ServerRouter();
        serverRouter.add(NodeId.of("A"), new KvChannel("localhost", 8080));
        serverRouter.add(NodeId.of("B"), new KvChannel("localhost", 8081));
        serverRouter.add(NodeId.of("C"), new KvChannel("localhost", 8082));
        kvClient = new KvClient(serverRouter);
    }

    @Test
    public void testSet() {
        kvClient.set("foo", "bar".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testGet() {
        byte[] bytes = kvClient.get("foo");
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }
}