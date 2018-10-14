package com.l2jbr.gameserver.gameserverpackets;

import com.l2jbr.gameserver.LoginServerThread.SessionKey;

/**
 * @author -Wooden-
 */
public class PlayerAuthRequest extends GameServerBasePacket {

	public PlayerAuthRequest(String account, SessionKey key) {
		writeC(0x05);
		writeS(account);
		writeD(key.sessionId);
		writeD(key.accountId);
		writeD(key.authAccountId);
		writeD(key.authKey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}