package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.network.L2GameClient;
import org.l2j.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author KenM
 */
public abstract class L2GameServerPacket extends WritablePacket<L2GameClient>
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class.getName());
	
	@Override
	protected void write()
	{
		try
		{
			writeImpl();
		}
		catch (Throwable t)
		{
			_log.error("Client: " + getClient().toString() + " - Failed writing: " + getType() + ";");
			t.printStackTrace();
		}
	}
	
	public void runImpl()
	{
		
	}
	
	protected abstract void writeImpl();
	
	/**
	 * @return A String with this packet name for debugging purposes
	 */
	public abstract String getType();
}
