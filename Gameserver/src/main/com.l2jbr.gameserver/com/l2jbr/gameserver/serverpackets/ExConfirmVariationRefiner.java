package com.l2jbr.gameserver.serverpackets;

/**
 * Format: (ch)ddddd
 */
public class ExConfirmVariationRefiner extends L2GameServerPacket
{
	private final int _refinerItemObjId;
	private final int _lifestoneItemId;
	private final int _gemstoneItemId;
	private final int _gemstoneCount;
	private final int _unk2;
	
	public ExConfirmVariationRefiner(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, int gemstoneCount)
	{
		_refinerItemObjId = refinerItemObjId;
		_lifestoneItemId = lifeStoneId;
		_gemstoneItemId = gemstoneItemId;
		_gemstoneCount = gemstoneCount;
		_unk2 = 1;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x53);
		writeInt(_refinerItemObjId);
		writeInt(_lifestoneItemId);
		writeInt(_gemstoneItemId);
		writeInt(_gemstoneCount);
		writeInt(_unk2);
	}

}
