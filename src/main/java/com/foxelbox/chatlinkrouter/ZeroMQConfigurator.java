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
