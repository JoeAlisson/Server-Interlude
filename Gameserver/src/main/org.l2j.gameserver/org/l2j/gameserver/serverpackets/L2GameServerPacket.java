package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public abstract class L2GameServerPacket extends WritablePacket<L2GameClient>  {
	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);
	
	@Override
	protected void write() {
		try {
			writeImpl();
		} catch (Throwable t) {
			_log.error("Client: {} - Failed writing {}", client, getClass().getSimpleName());
			_log.error(t.getLocalizedMessage(), t);
		}
	}
	
	public void runImpl() {  }
	
	protected abstract void writeImpl();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
