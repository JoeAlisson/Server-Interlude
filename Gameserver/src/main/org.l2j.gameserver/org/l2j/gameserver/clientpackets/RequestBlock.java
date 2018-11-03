/* This program is free software; you can redistribute it and/or modify
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

import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class RequestBlock extends L2GameClientPacket
{
	private static final String _C__A0_REQUESTBLOCK = "[C] A0 RequestBlock";
	private static Logger _log = LoggerFactory.getLogger(L2PcInstance.class.getName());
	
	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;
	
	private String _name;
	private Integer _type;
	private L2PcInstance _target;
	
	@Override
	protected void readImpl()
	{
		_type = readInt(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock
		
		if ((_type == BLOCK) || (_type == UNBLOCK))
		{
			_name = readString();
			_target = L2World.getInstance().getPlayer(_name);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		switch (_type)
		{
			case BLOCK:
			case UNBLOCK:
				if (_target == null)
				{
					// Incorrect reader name.
					activeChar.sendPacket(new SystemMessage(SystemMessageId.FAILED_TO_REGISTER_TO_IGNORE_LIST));
					return;
				}
				
				if (_target.isGM())
				{
					// Cannot block a GM character.
					activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_AN_A_GM));
					return;
				}
				
				if (_type == BLOCK)
				{
					BlockList.addToBlockList(activeChar, _target);
				}
				else
				{
					BlockList.removeFromBlockList(activeChar, _target);
				}
				break;
			case BLOCKLIST:
				BlockList.sendListToOwner(activeChar);
				break;
			case ALLBLOCK:
				BlockList.setBlockAll(activeChar, true);
				break;
			case ALLUNBLOCK:
				BlockList.setBlockAll(activeChar, false);
				break;
			default:
				_log.info("Unknown 0x0a block type: " + _type);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__A0_REQUESTBLOCK;
	}
}
