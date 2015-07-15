package com.foxelbox.chatlinkrouter;

import com.foxelbox.dependencies.config.Configuration;
import org.zeromq.ZMQ;

import java.io.File;
import java.nio.charset.Charset;

public class TestMain {
    private static final ZMQ.Context zmqContext = ZMQ.context(4);
    private static ZMQ.Socket sender;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private static Configuration configuration;

    public static void main(String[] args) {
        configuration = new Configuration(new File("."));

        sender = zmqContext.socket(ZMQ.PUB);
        sender.connect(configuration.getValue("zmq-server-to-broker", "tcp://127.0.0.1:5556"));

        final ZMQ.Socket receiver = zmqContext.socket(ZMQ.SUB);
        receiver.connect(configuration.getValue("zmq-broker-to-server", "tcp://127.0.0.1:5559"));
        receiver.subscribe(new byte[]{'{'});

        Thread t = new Thread() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    receiver.recv(0);
                    System.out.println("SUB: " + System.nanoTime());
                }
            }
        };
        t.setDaemon(true);
        t.setName("ZMQ SUB");
        t.start();

        sendMessage();
        sendMessage();
        sendMessage();
        sendMessage();
    }

    public static void sendMessage() {
        System.out.println("A");
        sender.send("{\"server\":\"Main\",\"from\":{\"uuid\":\"c413b466-06f0-4a53-b088-d6bc1eb1cd21\",\"name\":\"ratcraft\"},\"to\":{\"type\":\"all\",\"filter\":[]},\"timestamp\":1436978664,\"id\":30727,\"context\":\"2de58c43-c878-4c7c-ab6b-ddf7c32045b3\",\"finalizeContext\":true,\"type\":\"text\",\"importance\":0,\"contents\":\"Hello :3\"}");
        System.out.println("B");
    }
}
