package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ShowMiniMap;

public final class RequestShowMiniMap extends L2GameClientPacket {
	
	@Override
	protected void readImpl() {
		// trigger
	}
	
	@Override
	protected final void runImpl() {
		sendPacket(new ShowMiniMap(0));
	}

}
