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

        new Thread() {
            public void run() {
                ZMQ.proxy(serverToMe, meToLink, null);
            }
        }.start();

        new Thread() {
            public void run() {
                ZMQ.proxy(linkToMe, meToServer, null);
            }
        }.start();
    }
}
