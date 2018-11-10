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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.CharacterFriendRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AskAddFriendPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestFriendInvite extends L2GameClientPacket
{
	private static final String _C__5E_REQUESTFRIENDINVITE = "[C] 5E RequestFriendInvite";
	private static Logger _log = LoggerFactory.getLogger(RequestFriendInvite.class.getName());
	
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readString();
	}
	
	@Override
	protected void runImpl()
	{
		SystemMessage sm;
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2PcInstance friend = L2World.getInstance().getPlayer(_name);
		_name = Util.capitalizeFirst(_name); // FIXME: is it right to capitalize a nickname?
		
		if (friend == null) {
			// Target is not found in the game.
			sm = new SystemMessage(SystemMessageId.THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME);
			activeChar.sendPacket(sm);
			return;
		}
		else if (friend == activeChar)
		{
			// You cannot add yourself to your own friend list.
			sm = new SystemMessage(SystemMessageId.YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST);
			activeChar.sendPacket(sm);
			return;
		}

        CharacterFriendRepository repository = DatabaseAccess.getRepository(CharacterFriendRepository.class);
        if(repository.existsFriends(activeChar.getObjectId(), friend.getObjectId())) {
            sm = new SystemMessage(SystemMessageId.S1_ALREADY_IN_FRIENDS_LIST);
            sm.addString(_name);
            activeChar.sendPacket(sm);
        }  else  {
            if (!friend.isProcessingRequest()) {
                // requets to become friend
                activeChar.onTransactionRequest(friend);
                sm = new SystemMessage(SystemMessageId.S1_REQUESTED_TO_BECOME_FRIENDS);
                sm.addString(_name);
                AskAddFriendPacket ajf = new AskAddFriendPacket(activeChar.getName());
                friend.sendPacket(ajf);
                friend.sendPacket(sm);
            } else {
                sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
                activeChar.sendPacket(sm);
            }
        }
	}
	
	@Override
	public String getType()
	{
		return _C__5E_REQUESTFRIENDINVITE;
	}
}