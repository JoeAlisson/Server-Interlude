package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.VersionCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProtocolVersion extends L2GameClientPacket {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolVersion.class);
    private int _version;

    @Override
    protected void readImpl() {
        _version = readInt();
    }

    @Override
    protected void runImpl() {
        // this packet is never encrypted
        if (_version == -2) {
            logger.info("Ping received");
            // this is just a ping attempt from the new C2 client
            client.closeNow();
        } else if (_version != 64 && _version != 110) {
            logger.warn("Client: {} -> Protocol Revision: {} is invalid. Version  {} is supported. Closing connection. ", client, _version, 64);
            logger.warn("Wrong Protocol Version {}", _version);
            client.close(new VersionCheck(client.enableCrypt(), 0));
        } else {
            VersionCheck pk = new VersionCheck(client.enableCrypt(), 1);
            client.sendPacket(pk);
        }
    }
}
