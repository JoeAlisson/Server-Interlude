package org.l2j.gameserver.clientpackets;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.L2GameServerPacket;
import org.l2j.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * Packets received by the game server from clients
 * @author KenM
 */
public abstract class L2GameClientPacket extends ReadablePacket<L2GameClient> {
	private static final Logger logger = LoggerFactory.getLogger(L2GameClientPacket.class);

	@Override
	protected boolean read() {
		try {
			readImpl();
			return true;
		} catch (Throwable t) {
			logger.error("Client: {}  Failed read {}", client, getClass().getSimpleName());
			logger.error(t.getLocalizedMessage(), t);
		}
		return false;
	}

	protected abstract void readImpl();

	@Override
	public void run() {
		try {
			// flood protection
			if ((GameTimeController.getGameTicks() - client.packetsSentStartTick) > 10) {
				client.packetsSentStartTick = GameTimeController.getGameTicks();
				client.packetsSentInSec = 0;
			} else {
				client.packetsSentInSec++;
				if (client.packetsSentInSec > 12) {
					if (client.packetsSentInSec < 100) {
						sendPacket(new ActionFailed());
					}
					return;
				}
			}

			runImpl();
			if ((this instanceof MoveBackwardToLocation) || (this instanceof AttackRequest) || (this instanceof RequestMagicSkillUse))
			// could include pickup and talk too, but less is better
			{
				// Removes onspawn protection - reader has faster computer than
				// average
				if (getClient().getActiveChar() != null) {
					getClient().getActiveChar().onActionRequest();
				}
			}
		} catch (Throwable t) {
			logger.error("Client: {}  Failed running {}", client, getClass().getSimpleName());
			logger.error(t.getLocalizedMessage(), t);
		}
	}

	protected abstract void runImpl();

	protected final void sendPacket(L2GameServerPacket gsp) {
		if (nonNull(client)) {
			client.sendPacket(gsp);
		}
	}

	public String getType() {
		return getClass().getName();
	}
}
