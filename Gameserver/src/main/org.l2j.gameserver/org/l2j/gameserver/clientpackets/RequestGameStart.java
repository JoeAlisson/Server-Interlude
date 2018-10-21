package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient.GameClientState;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.CharacterSelectedPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

public class RequestGameStart extends L2GameClientPacket  {

	private static final Logger logger = LoggerFactory.getLogger(RequestGameStart.class);

	private int _charSlot;

	@Override
	protected void readImpl()  {
		_charSlot = readInt();
	}
	
	@Override
	protected void runImpl() {
		// we should always be able to acquire the lock
		// but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock()) {
			try {
				// should always be null
				// but if not then this is repeated packet and nothing should be done here
				if (isNull(client.getActiveChar())) {
					// The L2PcInstance must be created here, so that it can be attached to the L2GameClient
					logger.debug("selected slot: {}", _charSlot);
					
					// load up character from disk
					L2PcInstance cha = client.loadCharFromDisk(_charSlot);
					if (isNull(cha)) {
						logger.error("Character could not be loaded (slot: {})", _charSlot);
						sendPacket(new ActionFailed());
						return;
					}
					if (cha.getAccessLevel() < 0)  {
						cha.closeNetConnection();
						return;
					}
					
					cha.setClient(client);
					client.setActiveChar(cha);
					
					client.setState(GameClientState.IN_GAME);
					sendPacket(new CharacterSelectedPacket(cha));
				}
			}
			finally {
				client.getActiveCharLock().unlock();
			}
		}
	}
}
