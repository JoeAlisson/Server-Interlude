package com.l2jbr.loginserver.network.serverpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.loginserver.GameServerTable;
import com.l2jbr.loginserver.network.gameserverpackets.ServerStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ServerList Format: cc [cddcchhcdc]
 * c: server list size (number of servers)
 * c: last server
 * [ (repeat for each servers)
 * c: server id (ignored by client?)
 * d: server ip
 * d: server port
 * c: age limit (used by client?)
 * c: pvp or not (used by client?)
 * h: current number of players
 * h: max number of players
 * c: 0 if server is down
 * d: 2nd bit: clock
 *    3rd bit: wont display server name
 *    4th bit: test server (used by client?)
 * c: 0 if you don't want to display brackets in front of sever name
 * ]
 *
 * Server will be considered as
 *  Good when the number of online players is less than half the maximum.
 *  as Normal between half and 4/5 and Full when there's more than 4/5 of the maximum number of players
 */
public final class ServerList extends L2LoginServerPacket {

    @Override
    public void write() {
        var servers = GameServerTable.getInstance().getRegisteredGameServers();
        writeByte(0x04);
        writeByte(servers.size());
        writeByte(client.getLastServer());

        for (var server : servers.values()) {
            writeByte(server.getId());

            var host = client.usesInternalIP() ? server.getInternalHost() : server.getExternalHost();

            try {
                var ip = InetAddress.getByName(host);
                var address = ip.getAddress();
                writeByte(Byte.toUnsignedInt(address[0]));
                writeByte(Byte.toUnsignedInt(address[1]));
                writeByte(Byte.toUnsignedInt(address[2]));
                writeByte(Byte.toUnsignedInt(address[3]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
                writeByte(127);
                writeByte(0);
                writeByte(0);
                writeByte(1);
            }

            writeInt(server.getPort());
            writeByte(0x00); // minimum age
            writeByte(server.isPvp() ? 1 : 0);
            writeShort(server.getOnlinePlayersCount());
            writeShort(server.getMaxPlayers());

            var status = server.getStatus();
            if(ServerStatus.STATUS_GM_ONLY == status && client.getAccessLevel() < Config.GM_MIN) {
                status = ServerStatus.STATUS_DOWN;
            }

            writeByte(ServerStatus.STATUS_DOWN == status ? 0x00 : 0x01);

            var bits = 0;

            if (server.isTestServer()) {
                bits |= 0x04;
            }

            if (server.isShowingClock()) {
                bits |= 0x02;
            }

            writeInt(1 << 10);
            writeByte(server.isShowingBrackets() ? 0x01 : 0x00); // Region
        }
        writeShort(8);
        writeByte(servers.size());
        for (var server : servers.values()) {
            writeByte(server.getId());
            writeByte(1);
            writeByte(1);
            writeByte(13213);
        }
    }

    @Override
    protected int packetSize() {
        int servers = GameServerTable.getInstance().getRegisteredGameServers().size();
        return super.packetSize() + 6 + servers * 24;
    }
}
