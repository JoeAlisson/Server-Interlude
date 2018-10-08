package com.l2jbr.loginserver.network;

import com.l2jbr.loginserver.network.AuthClient.LoginClientState;
import com.l2jbr.loginserver.network.clientpackets.AuthGameGuard;
import com.l2jbr.loginserver.network.clientpackets.RequestAuthLogin;
import com.l2jbr.loginserver.network.clientpackets.RequestServerList;
import com.l2jbr.loginserver.network.clientpackets.RequestServerLogin;
import org.l2j.mmocore.DataWrapper;
import org.l2j.mmocore.PacketHandler;
import org.l2j.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for packets received by Login Server
 * @author KenM
 */
public final class L2LoginPacketHandler implements PacketHandler<AuthClient> {

    private static final Logger logger = LoggerFactory.getLogger(L2LoginPacketHandler.class);

	@Override
	public ReadablePacket<AuthClient> handlePacket(DataWrapper data, AuthClient client) {
        int opcode = Byte.toUnsignedInt(data.get());

        ReadablePacket<AuthClient> packet = null;
        LoginClientState state = client.getState();

        switch (state) {
            case CONNECTED:
                if (opcode == 0x07) {
                    packet = new AuthGameGuard();
                } else {
                    debugOpcode(opcode, data, state);
                }
                break;
            case AUTHED_GG:
                if (opcode == 0x00) {
                    packet = new RequestAuthLogin();
                } else {
                    debugOpcode(opcode, data, state);
                }
                break;
            case AUTHED_LOGIN:
                if (opcode == 0x05) {
                    packet = new RequestServerList();
                }
                else if (opcode == 0x02) {
                    packet = new RequestServerLogin();
                } else {
                    debugOpcode(opcode, data, state);
                }
                break;
        }
        return packet;
	}

	private void debugOpcode(int opcode, DataWrapper data, LoginClientState state) {
	    logger.warn("Unknown Opcode: {} for state {}\n {}",  Integer.toHexString(opcode), state, data.expose());
	}
}
