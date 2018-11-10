package org.l2j.gameserver.network.serverpackets;

public class ExConfirmVariationGemstone extends L2GameServerPacket
{
	private final int _gemstoneObjId;
	private final int _unk1;
	private final int _gemstoneCount;
	private final int _unk2;
	private final int _unk3;
	
	public ExConfirmVariationGemstone(int gemstoneObjId, int count)
	{
		_gemstoneObjId = gemstoneObjId;
		_unk1 = 1;
		_gemstoneCount = count;
		_unk2 = 1;
		_unk3 = 1;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x54);
		writeInt(_gemstoneObjId);
		writeInt(_unk1);
		writeInt(_gemstoneCount);
		writeInt(_unk2);
		writeInt(_unk3);
	}
}
