package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.AuthServer;
import org.l2j.authserver.network.client.packet.GameServerWritablePacket;

/**
 *  ID: 0x00
 * 	format:
 * 	  d proto rev
 * 	  d key size
 * 	  b key
 *
 * @author -Wooden-
 */
public class InitLS extends GameServerWritablePacket {
	
	public InitLS(byte[] publickey) {
		writeByte(0x00);
		writeInt(AuthServer.PROTOCOL_REV);
		writeInt(publickey.length);
		writeBytes(publickey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
