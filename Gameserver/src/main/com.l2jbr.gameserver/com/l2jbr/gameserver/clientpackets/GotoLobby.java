package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.serverpackets.CharSelectInfo;

public class GotoLobby extends L2GameClientPacket {
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new CharSelectInfo(client.getAccountName(), client.getSessionId().sessionId));
	}

    @Override
    public String getType() {
        return "[C] GOTO_LOBBY";
    }
}