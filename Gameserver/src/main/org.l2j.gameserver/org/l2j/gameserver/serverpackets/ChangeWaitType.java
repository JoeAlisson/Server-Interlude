package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2Character;

public class ChangeWaitType extends L2GameServerPacket {

	private final int _charObjId;
	private final int _moveType;
	private final int _x, _y, _z;
	
	public static final int WT_SITTING = 0;
	public static final int WT_STANDING = 1;
	public static final int WT_START_FAKEDEATH = 2;
	public static final int WT_STOP_FAKEDEATH = 3;
	
	public ChangeWaitType(L2Character character, int newMoveType) {
		_charObjId = character.getObjectId();
		_moveType = newMoveType;
		
		_x = character.getX();
		_y = character.getY();
		_z = character.getZ();
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0x2f);
		writeInt(_charObjId);
		writeInt(_moveType);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
	}

}
