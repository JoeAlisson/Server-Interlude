package org.l2j.gameserver.network.clientpackets;


import org.l2j.gameserver.network.serverpackets.ExShowCastleInfo;

public class RequestAllCastleInfo extends L2GameClientPacket {
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new ExShowCastleInfo());
	}
}