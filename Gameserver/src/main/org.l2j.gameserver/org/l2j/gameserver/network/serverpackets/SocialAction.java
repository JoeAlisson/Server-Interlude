package org.l2j.gameserver.network.serverpackets;

public class SocialAction extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _actionId;

	public SocialAction(int playerId, int actionId)
	{
		_charObjId = playerId;
		_actionId = actionId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x2d);
		writeInt(_charObjId);
		writeInt(_actionId);
	}
}
