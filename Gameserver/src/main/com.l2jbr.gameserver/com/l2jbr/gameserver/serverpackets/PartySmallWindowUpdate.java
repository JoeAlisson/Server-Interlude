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
package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;


/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.5 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private static final String _S__67_PARTYSMALLWINDOWUPDATE = "[S] 52 PartySmallWindowUpdate";
	private final L2PcInstance _member;
	
	public PartySmallWindowUpdate(L2PcInstance member)
	{
		_member = member;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x52);
		writeInt(_member.getObjectId());
		writeString(_member.getName());
		
		writeInt((int) _member.getCurrentCp()); // c4
		writeInt(_member.getMaxCp()); // c4
		
		writeInt((int) _member.getCurrentHp());
		writeInt(_member.getMaxHp());
		writeInt((int) _member.getCurrentMp());
		writeInt(_member.getMaxMp());
		writeInt(_member.getLevel());
		writeInt(_member.getPlayerClass().getId());
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jbr.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__67_PARTYSMALLWINDOWUPDATE;
	}
}
