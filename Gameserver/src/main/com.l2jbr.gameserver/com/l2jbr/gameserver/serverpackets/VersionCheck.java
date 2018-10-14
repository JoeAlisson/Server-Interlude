package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;

public final class VersionCheck extends L2GameServerPacket {

    private static final String _S__01_KEYPACKET = "[S] 01 VersionCheck";
    private static final int CLASSIC_SERVER = 1024;

    private final byte[] key;
    private final int versionMatch;

    public VersionCheck(byte[] key, int versionMatch) {
		this.key = key;
		this.versionMatch = versionMatch;
	}
	
	@Override
	public void writeImpl() {
		writeByte(0x2E);
		writeByte(versionMatch);
        for (int i = 0; i < 8 ; i++) {
            writeByte(key[i]);
        }
		writeInt(0x01);
        writeInt(Config.SERVER_ID);
        writeByte(0x01);
		writeInt(0x00); // Seed Obfuscation Key
        writeByte((Config.SERVER_TYPE & CLASSIC_SERVER) == CLASSIC_SERVER ? 1 : 0);
        writeByte(0x00); // Queued Login
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.L2GameServerPacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__01_KEYPACKET;
	}
	
}
