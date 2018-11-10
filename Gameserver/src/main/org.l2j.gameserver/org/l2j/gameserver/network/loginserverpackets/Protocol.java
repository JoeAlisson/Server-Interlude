package org.l2j.gameserver.network.loginserverpackets;


public class Protocol extends LoginServerBasePacket
{
	private final int _rev;
	private final byte[] _key;
	
	public int getRevision()
	{
		return _rev;
	}
	
	public byte[] getRSAKey()
	{
		return _key;
	}

	public Protocol(byte[] decrypt)
	{
		super(decrypt);
		_rev = readD();
		int size = readD();
		_key = readB(size);
	}
	
}