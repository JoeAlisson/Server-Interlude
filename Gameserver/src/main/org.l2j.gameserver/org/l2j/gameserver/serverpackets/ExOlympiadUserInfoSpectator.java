package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class ExOlympiadUserInfoSpectator extends L2GameServerPacket
{
	// chcdSddddd
	private static final String _S__FE_29_OLYMPIADUSERINFOSPECTATOR = "[S] FE:29 OlympiadUserInfoSpectator";
	private static int _side;
	private static L2PcInstance _player;

	public ExOlympiadUserInfoSpectator(L2PcInstance player, int side)
	{
		_player = player;
		_side = side;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x29);
		writeByte(_side);
		writeInt(_player.getObjectId());
		writeString(_player.getName());
		writeInt(_player.getPlayerClass().getId());
		writeInt((int) _player.getCurrentHp());
		writeInt(_player.getMaxHp());
		writeInt((int) _player.getCurrentCp());
		writeInt(_player.getMaxCp());
	}
}