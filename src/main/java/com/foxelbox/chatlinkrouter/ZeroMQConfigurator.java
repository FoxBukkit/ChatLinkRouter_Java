package com.foxelbox.chatlinkrouter;

import org.zeromq.ZMQ;

public class ZeroMQConfigurator {
    public static void parseZeroMQConfig(String config, ZMQ.Socket socket) {
        String[] values = config.split(";");
        for(int i = 0; i < values.length; i += 2) {
            switch(values[i].toLowerCase()) {
                case "connect":
                    socket.connect(values[i + 1]);
                    break;
                case "bind":
                    socket.bind(values[i + 1]);
                    break;
            }
        }
    }

    public static String getDefaultConfig(String mode, int port) {
        return mode + ";tcp://127.0.0.1:" + port;
    }
}
