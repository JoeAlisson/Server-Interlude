package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ItemListPacket;

import static java.util.Objects.isNull;


public final class RequestItemList extends L2GameClientPacket {

	@Override
	protected void readImpl() {
		// trigger
	}
	
	@Override
	protected void runImpl() {
		// TODO send only if is updatable
		var player = client.getActiveChar();
		if (isNull(player) ||  player.isInvetoryDisabled()) {
		    sendPacket(new ActionFailed());
		} else {
            ItemListPacket il = new ItemListPacket(player, true);
            sendPacket(il);
        }
	}

}
