package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.serverpackets.CharSelectInfo;

public class GotoLobby extends L2GameClientPacket {
	@Override
	protected void readImpl() { }

	@Override
	protected void runImpl() {
		var selectInfo = new CharSelectInfo(client.getCharacters(), -1);
		sendPacket(selectInfo);
	}

    @Override
    public String getType() {
        return "[C] GOTO_LOBBY";
    }
}