package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.serverpackets.CharSelectInfo;

public class GotoLobby extends L2GameClientPacket {
	@Override
	protected void readImpl() { }

	@Override
	protected void runImpl() {
		sendPacket(new CharSelectInfo());
	}
}