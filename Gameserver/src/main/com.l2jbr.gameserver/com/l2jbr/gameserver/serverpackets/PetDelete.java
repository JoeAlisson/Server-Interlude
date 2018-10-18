package com.l2jbr.gameserver.serverpackets;

public class PetDelete extends L2GameServerPacket
{
	private final int _petId;
	private final int _petObjId;
	
	public PetDelete(int petId, int petObjId)
	{
		_petId = petId; // summonType?
		_petObjId = petObjId; // objectId
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xb6);
		writeInt(_petId);// dont really know what these two are since i never needed them
		writeInt(_petObjId);// objectId
	}

}
