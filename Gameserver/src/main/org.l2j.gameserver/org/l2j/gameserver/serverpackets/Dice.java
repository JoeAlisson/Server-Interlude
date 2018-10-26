package org.l2j.gameserver.serverpackets;

public class Dice extends L2GameServerPacket
{

	private final int _charObjId;
	private final int _itemId;
	private final int _number;
	private final int _x;
	private final int _y;
	private final int _z;

	public Dice(int charObjId, int itemId, int number, int x, int y, int z)
	{
		_charObjId = charObjId;
		_itemId = itemId;
		_number = number;
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xD4);
		writeInt(_charObjId); // object id of player
		writeInt(_itemId); // item id of dice (spade) 4625,4626,4627,4628
		writeInt(_number); // number rolled
		writeInt(_x); // x
		writeInt(_y); // y
		writeInt(_z); // z
	}

}