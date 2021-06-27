package org.ywb.raft.kvstore;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @author yuwenbo1
 * @date 2021/6/27 9:29 下午 星期日
 * @since 1.0.0
 */
public class AllOptionals {

    public static Options getOptions() {
        final Options options = new Options();
        options.addOption(
                Option.builder("m")
                        .hasArg()
                        .argName("mode")
                        .desc("start mode, available: standalone, standby, group-member. default is standalone")
                        .build()
        );
        options.addOption(
                Option.builder("i")
                        .longOpt("id")
                        .hasArg()
                        .argName("node-id")
                        .required()
                        .desc("node id, required. must be unique in group. if start with mode group-member, please ensure id in group config")
                        .build()
        );
        options.addOption(
                Option.builder("h")
                        .hasArg()
                        .argName("host")
                        .desc("host, required when status with standalone or standby mode")
                        .build()
        );
        options.addOption(
                Option.builder("p1")
                        .longOpt("port-raft-node")
                        .hasArg()
                        .argName("port")
                        .type(Number.class)
                        .desc("port of raft node, required when start with standalone or standby mode")
                        .build()
        );
        options.addOption(
                Option.builder("p2")
                        .hasArg()
                        .longOpt("port-service")
                        .argName("port")
                        .type(Number.class)
                        .required()
                        .desc("port of service,required")
                        .build()
        );
        options.addOption(
                Option.builder("d")
                        .hasArg()
                        .argName("data-dir")
                        .desc("data directory, optional. must be present")
                        .build()
        );
        options.addOption(
                Option.builder("gc")
                        .hasArg()
                        .argName("node-endpoint")
                        .desc("group config, required when starts with group-number mode. format:<node-group> <node-endpoint>..., format of node-endpoint: " +
                                "<node-id>,<host>,<port-raft-node>, eg: A,localhost,8000,B,localhost,8010")
                        .build()
        );
        return options;
    }

}
