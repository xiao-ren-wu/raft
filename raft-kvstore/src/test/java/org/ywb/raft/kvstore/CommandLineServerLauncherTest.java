package org.ywb.raft.kvstore;


import org.junit.jupiter.api.Test;

class CommandLineServerLauncherTest {


    @Test
    public void test() {
        String[] args = new String[]{
          "-gc",
          "A,localhost,2333",
          "B,localhost,2334",
          "C,localhost,2345",
          "-m",
          "group-member",
          "-i",
          "A",
          "-p2",
          "3938",
                "-d",
                "/Users/yuwenbo/IdeaProjects/raft/raft-kvstore/target/raft3/"
        };
        new CommandLineServerLauncher().start();
    }
}