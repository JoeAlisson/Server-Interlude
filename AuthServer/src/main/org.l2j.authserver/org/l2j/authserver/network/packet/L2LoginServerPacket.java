package org.l2j.authserver.network.packet;

import org.l2j.authserver.network.AuthClient;
import org.l2j.mmocore.WritablePacket;

/**
 * @author KenM
 */
public abstract class L2LoginServerPacket extends WritablePacket<AuthClient> {

    @Override
    protected int packetSize() {
        return 22; // HEADER + CHECKSUM + PADDING
    }
}
