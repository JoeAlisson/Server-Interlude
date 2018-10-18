package com.l2jbr.gameserver.serverpackets;

/**
 * Format: (ch) ddd b d: ? d: crest ID d: crest size b: raw data
 * @author -Wooden-
 */
public class ExPledgeCrestLarge extends L2GameServerPacket
{
	private static final String _S__FE_28_EXPLEDGECRESTLARGE = "[S] FE:28 ExPledgeCrestLarge";
	private final int _crestId;
	private final byte[] _data;
	
	public ExPledgeCrestLarge(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x28);
		
		writeInt(0x00); // ???
		writeInt(_crestId);
		writeInt(_data.length);
		
		writeBytes(_data);
		
	}
}