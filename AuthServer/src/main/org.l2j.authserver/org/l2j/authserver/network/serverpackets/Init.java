package org.l2j.authserver.network.serverpackets;

import org.l2j.authserver.network.AuthClient;

/**
 * Format: dd b dddd s
 * d: session id
 * d: protocol revision
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 * 				   0x10 bytes at 0x00
 * d: unknow
 * d: unknow
 * d: unknow
 * d: unknow
 * s: blowfish key
 */
public final class Init extends L2LoginServerPacket {

	private final int _sessionId;
	
	private final byte[] _publicKey;
	private final byte[] _blowfishKey;
	
	public Init(AuthClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}
	
	public Init(byte[] publickey, byte[] blowfishkey, int sessionId)
	{
		_sessionId = sessionId;
		_publicKey = publickey;
		_blowfishKey = blowfishkey;
	}
	
	@Override
	protected void write()
	{
		writeByte(0x00); // init packet id
		
		writeInt(_sessionId); // session id
		writeInt(0xc621); // protocol revision
		
		writeBytes(_publicKey); // RSA Public Key
		
		// unk GG related?
		writeInt(0x29DD954E);
		writeInt(0x77C39CFC);
		writeInt(0x97ADB620);
		writeInt(0x07BDE0F7);
		
		writeBytes(_blowfishKey); // BlowFish key  // 16
		writeByte(0x00); // null termination ;)
	}

	@Override
	protected int packetSize() {
		return super.packetSize() + 172;
	}
}
