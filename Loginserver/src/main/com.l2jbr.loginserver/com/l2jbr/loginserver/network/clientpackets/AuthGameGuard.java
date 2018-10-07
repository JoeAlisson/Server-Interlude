package com.l2jbr.loginserver.network.clientpackets;

import com.l2jbr.loginserver.network.L2LoginClient.LoginClientState;
import com.l2jbr.loginserver.network.serverpackets.GGAuth;
import com.l2jbr.loginserver.network.serverpackets.LoginFail.LoginFailReason;

import java.util.Objects;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends L2LoginClientPacket {

	private int _sessionId;

    /**
	 * @see com.l2jbr.loginserver.network.clientpackets.L2LoginClientPacket#readImpl()
	 */
	@Override
	protected boolean readImpl()
	{
		if (availableData() >= 20) {
			_sessionId = readInt();
            int _data1 = readInt();
            int _data2 = readInt();
            int _data3 = readInt();
            int _data4 = readInt();
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		if (Objects.equals(_sessionId, client.getSessionId())) {
			client.setState(LoginClientState.AUTHED_GG);
			client.sendPacket(new GGAuth(_sessionId));
		} else {
			client.close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
