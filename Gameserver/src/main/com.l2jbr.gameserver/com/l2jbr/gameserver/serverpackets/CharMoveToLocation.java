package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Character;

public class CharMoveToLocation extends L2GameServerPacket {

	private final int _charObjId, _x, _y, _z, _xDst, _yDst, _zDst;
	
	public CharMoveToLocation(L2Character cha) {
		_charObjId = cha.getObjectId();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_xDst = cha.getXdestination();
		_yDst = cha.getYdestination();
		_zDst = cha.getZdestination();
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x2F);
		
		writeInt(_charObjId);
		
		writeInt(_xDst);
		writeInt(_yDst);
		writeInt(_zDst);
		
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}
}
