package com.l2jbr.loginserver.network.loginserverpackets;

import com.l2jbr.loginserver.L2LoginServer;
import com.l2jbr.loginserver.network.serverpackets.ServerBasePacket;

/**
 *  ID: 0x00
 * 	format:
 * 	  d proto rev
 * 	  d key size
 * 	  b key
 *
 * @author -Wooden-
 */
public class InitLS extends ServerBasePacket {
	
	public InitLS(byte[] publickey) {
		writeByte(0x00);
		writeInt(L2LoginServer.PROTOCOL_REV);
		writeInt(publickey.length);
		writeBytes(publickey);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
