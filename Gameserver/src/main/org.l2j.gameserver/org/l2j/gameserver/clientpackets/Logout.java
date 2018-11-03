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
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.Olympiad;
import org.l2j.gameserver.SevenSignsFestival;
import org.l2j.gameserver.communitybbs.Manager.RegionBBSManager;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.TvTEvent;
import org.l2j.gameserver.model.entity.database.repository.CharacterFriendRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.ActionFailed;
import org.l2j.gameserver.serverpackets.FriendList;
import org.l2j.gameserver.serverpackets.SystemMessage;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;


/**
 * This class ...
 * @version $Revision: 1.9.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class Logout extends L2GameClientPacket
{
	private static final String _C__09_LOGOUT = "[C] 09 Logout";
	private static Logger _log = LoggerFactory.getLogger(Logout.class.getName());
	
	@Override
	protected void readImpl()
	{
		
	}
	
	@Override
	protected void runImpl()
	{
		// Dont allow leaving if reader is fighting
		L2PcInstance player = getClient().getActiveChar();
		
		if (player == null)
		{
			return;
		}
		
		player.getInventory().updateDatabase();
		
		if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player))
		{
			if (Config.DEBUG)
			{
				_log.debug("Player " + player.getName() + " tried to logout while fighting");
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.CANT_LOGOUT_WHILE_FIGHTING));
			player.sendPacket(new ActionFailed());
			return;
		}
		
		if (player.atEvent)
		{
			player.sendPacket(SystemMessage.sendString("A superior power doesn't allow you to leave the event"));
			return;
		}
		
		if (player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
		{
			player.sendMessage("You cant logout in olympiad mode");
			return;
		}
		
		// Prevent reader from logging out if they are a festival participant
		// and it is in progress, otherwise notify party members that the reader
		// is not longer a participant.
		if (player.isFestivalParticipant())
		{
			if (SevenSignsFestival.getInstance().isFestivalInitialized())
			{
				player.sendMessage("You cannot log out while you are a participant in a festival.");
				return;
			}
			L2Party playerParty = player.getParty();
			
			if (playerParty != null)
			{
				player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName() + " has been removed from the upcoming festival."));
			}
		}
		if (player.isFlying())
		{
			player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
		}
		
		TvTEvent.onLogout(player);
		RegionBBSManager.getInstance().changeCommunityBoard();
		
		player.deleteMe();
        // Close the connection with the client
        player.closeNetConnection();
		notifyFriends(player);
	}
	
	private void notifyFriends(L2PcInstance cha) {
        CharacterFriendRepository repository = DatabaseAccess.getRepository(CharacterFriendRepository.class);
        repository.findAllByCharacterId(cha.getObjectId()).forEach(characterFriends -> {
            L2PcInstance friend = L2World.getInstance().getPlayer(characterFriends.getFriendName());
            if(nonNull(friend)) {
                friend.sendPacket(new FriendList(friend));
            }
        });
	}
	
	@Override
	public String getType()
	{
		return _C__09_LOGOUT;
	}
}