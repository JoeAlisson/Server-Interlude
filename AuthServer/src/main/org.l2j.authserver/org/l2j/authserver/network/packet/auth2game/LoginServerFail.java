package org.l2j.authserver.network.packet.auth2game;

import org.l2j.authserver.network.packet.ServerBasePacket;

/**
 * @author -Wooden-
 */
public class LoginServerFail extends ServerBasePacket {

	public LoginServerFail(int reason)
	{
		writeByte(0x01);
		writeByte(reason);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
	
	public static final int REASON_IP_BANNED = 1;
	public static final int REASON_IP_RESERVED = 2;
	public static final int REASON_WRONG_HEXID = 3;
	public static final int REASON_ID_RESERVED = 4;
	public static final int REASON_NO_FREE_ID = 5;
	public static final int NOT_AUTHED = 6;
	public static final int REASON_ALREADY_LOGGED = 7;
	
}