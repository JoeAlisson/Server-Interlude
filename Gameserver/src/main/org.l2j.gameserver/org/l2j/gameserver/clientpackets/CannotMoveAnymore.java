/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.gameserver.clientpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.ai.Event;
import org.l2j.gameserver.model.L2Position;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.serverpackets.PartyMemberPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class CannotMoveAnymore extends L2GameClientPacket
{
	private static final String _C__36_STOPMOVE = "[C] 36 CannotMoveAnymore";
	
	private static Logger _log = LoggerFactory.getLogger(CannotMoveAnymore.class.getName());
	
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	
	@Override
	protected void readImpl()
	{
		_x = readInt();
		_y = readInt();
		_z = readInt();
		_heading = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		L2Character player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (Config.DEBUG)
		{
			_log.debug("client: x:" + _x + " y:" + _y + " z:" + _z + " server x:" + player.getX() + " y:" + player.getY() + " z:" + player.getZ());
		}
		if (player.getAI() != null)
		{
			player.getAI().notifyEvent(Event.EVT_ARRIVED_BLOCKED, new L2Position(_x, _y, _z, _heading));
		}
		if ((player instanceof L2PcInstance) && (((L2PcInstance) player).getParty() != null))
		{
			((L2PcInstance) player).getParty().broadcastToPartyMembers(((L2PcInstance) player), new PartyMemberPosition((L2PcInstance) player));
		}
		
		// reader.stopMove();
		//
		// if (Config.DEBUG)
		// logger.debug("client: x:"+_x+" y:"+_y+" z:"+_z+
		// " server x:"+reader.getX()+" y:"+reader.getZ()+" z:"+reader.getZ());
		// StopMove smwl = new StopMove(reader);
		// getClient().getActiveChar().sendPacket(smwl);
		// getClient().getActiveChar().broadcastPacket(smwl);
		//
		// StopRotation sr = new StopRotation(getClient().getActiveChar(),
		// heading);
		// getClient().getActiveChar().sendPacket(sr);
		// getClient().getActiveChar().broadcastPacket(sr);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__36_STOPMOVE;
	}
}
