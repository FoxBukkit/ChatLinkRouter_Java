/**
 * This file is part of FoxBukkitChatLinkRouter.
 *
 * FoxBukkitChatLinkRouter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkitChatLinkRouter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkitChatLinkRouter.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.chatlinkrouter;

import org.zeromq.ZMQ;

import java.nio.charset.Charset;

public class TestMain {
    private static final ZMQ.Context zmqContext = ZMQ.context(4);
    private static ZMQ.Socket sender;
    private static final Charset CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) {
        sender = zmqContext.socket(ZMQ.PUSH);
        sender.connect("tcp://127.0.0.1:5556");

        final ZMQ.Socket receiver = zmqContext.socket(ZMQ.SUB);
        receiver.connect("tcp://127.0.0.1:5559");
        receiver.subscribe("CMO".getBytes());

        Thread t = new Thread() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    System.out.println(receiver.recvStr(CHARSET));
                }
            }
        };
        //t.setDaemon(true);
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
