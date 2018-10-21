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

package org.l2j.gameserver.handler.usercommandhandlers;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Clan;
import org.l2j.gameserver.model.entity.database.repository.ClanRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.serverpackets.SystemMessage;


/**
 * Support for /clanwarlist command
 * @author Tempy
 */
public class ClanWarsList implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		88,
		89,
		90
	};
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IUserCommandHandler#useUserCommand(int, org.l2j.gameserver.model.L2PcInstance)
	 */
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if ((id != COMMAND_IDS[0]) && (id != COMMAND_IDS[1]) && (id != COMMAND_IDS[2]))
		{
			return false;
		}
		
		L2Clan clan = activeChar.getClan();
		
		if (clan == null)
		{
			activeChar.sendMessage("You are not in a clan.");
			return false;
		}

        Iterable<Clan> clans;
        ClanRepository repository = DatabaseAccess.getRepository(ClanRepository.class);
        if (id == 88) {
            // Attack List
            activeChar.sendPacket(new SystemMessage(SystemMessageId.CLANS_YOU_DECLARED_WAR_ON));
            clans = repository.findAllByOnlyAttacker(clan.getClanId());
        }
        else if (id == 89) {
            // Under Attack List
            activeChar.sendPacket(new SystemMessage(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU));
            clans = repository.findAllByOnlyUnderAttack(clan.getClanId());
        }
        else { // ID = 90
            // War List
            activeChar.sendPacket(new SystemMessage(SystemMessageId.WAR_LIST));
            clans = repository.findAllInWar(clan.getClanId());
        }

        clans.forEach(clanModel -> {
            String clanName = clanModel.getName();
            int ally_id = clanModel.getAllyId();

            SystemMessage sm;
            if (ally_id > 0) {
                // Target With Ally
                sm = new SystemMessage(SystemMessageId.S1_S2_ALLIANCE);
                sm.addString(clanName);
                sm.addString(clanModel.getAllyName());
            }
            else  {
                // Target Without Ally
                sm = new SystemMessage(SystemMessageId.S1_NO_ALLI_EXISTS);
                sm.addString(clanName);
            }
            activeChar.sendPacket(sm);
        });

        activeChar.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_FOOT));
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.handler.IUserCommandHandler#getUserCommandList()
	 */
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
