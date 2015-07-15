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

import com.foxelbox.dependencies.config.Configuration;
import org.zeromq.ZMQ;

import java.io.File;
import java.io.IOException;

public class Main {
    public static Configuration configuration;

    public static ZMQ.Context zmqContext;

    private static void moveElements(final ZMQ.Socket from, final ZMQ.Socket to) {
        boolean more;
        do {
            byte[] msg = from.recv(0);
            more = from.hasReceiveMore();
            to.send(msg, more ? ZMQ.SNDMORE : 0);
        } while(more);
    }

    public static void main(String[] args) throws IOException {
        configuration = new Configuration(new File("."));
        zmqContext = ZMQ.context(4);

        final ZMQ.Socket serverToMe = zmqContext.socket(ZMQ.PULL);
        ZeroMQConfigurator.parseZeroMQConfig(configuration.getValue("zmq-server-to-broker", ZeroMQConfigurator.getDefaultConfig("bind", 5556)), serverToMe);
        final ZMQ.Socket meToLink = zmqContext.socket(ZMQ.PUSH);
        ZeroMQConfigurator.parseZeroMQConfig(configuration.getValue("zmq-broker-to-link", ZeroMQConfigurator.getDefaultConfig("connect", 5557)), meToLink);

        final ZMQ.Socket linkToMe = zmqContext.socket(ZMQ.XSUB);
        ZeroMQConfigurator.parseZeroMQConfig(configuration.getValue("zmq-link-to-broker", ZeroMQConfigurator.getDefaultConfig("connect", 5558)), linkToMe);
        final ZMQ.Socket meToServer = zmqContext.socket(ZMQ.XPUB);
        ZeroMQConfigurator.parseZeroMQConfig(configuration.getValue("zmq-broker-to-server", ZeroMQConfigurator.getDefaultConfig("bind", 5559)), meToServer);

        final ZMQ.Poller poller1 = new ZMQ.Poller(2);
        poller1.register(serverToMe, ZMQ.Poller.POLLIN);
        poller1.register(meToLink, ZMQ.Poller.POLLIN);

        final ZMQ.Poller poller2 = new ZMQ.Poller(2);
        poller2.register(linkToMe, ZMQ.Poller.POLLIN);
        poller2.register(meToServer, ZMQ.Poller.POLLIN);

        new Thread() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    poller1.poll();
                    if(poller1.pollin(0)) {
                        moveElements(serverToMe, meToLink);
                    }
                    if(poller1.pollin(1)) {
                        moveElements(meToLink, serverToMe);
                    }
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    poller2.poll();
                    if (poller2.pollin(0)) {
                        moveElements(linkToMe, meToServer);
                    }
                    if (poller2.pollin(1)) {
                        moveElements(meToServer, linkToMe);
                    }
                }
            }
        }.start();
    }
}
