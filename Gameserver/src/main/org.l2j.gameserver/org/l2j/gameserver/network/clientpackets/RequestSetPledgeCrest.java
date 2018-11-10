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
import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.factory.IdFactory;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.repository.ClanRepository;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestSetPledgeCrest extends L2GameClientPacket
{
	private static final String _C__53_REQUESTSETPLEDGECREST = "[C] 53 RequestSetPledgeCrest";
	static Logger _log = LoggerFactory.getLogger(RequestSetPledgeCrest.class.getName());
	
	private int _length;
	private byte[] _data;
	
	@Override
	protected void readImpl()
	{
		_length = readInt();
		if ((_length < 0) || (_length > 256))
		{
			return;
		}
		
		_data = new byte[_length];
		readBytes(_data);
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		L2Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}
		
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS));
			return;
		}
		
		if (_length < 0)
		{
			activeChar.sendMessage("File transfer error.");
			return;
		}
		if (_length > 256)
		{
			activeChar.sendMessage("The clan crest file size was too big (max 256 bytes).");
			return;
		}
		if ((_length == 0) || (_data.length == 0))
		{
			CrestCache.getInstance().removePledgeCrest(clan.getCrestId());
			
			clan.setHasCrest(false);
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CLAN_CREST_HAS_BEEN_DELETED));
			
			for (L2PcInstance member : clan.getOnlineMembers(""))
			{
				member.broadcastUserInfo();
			}
			
			return;
		}
		
		if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_REGISTER_CREST) == L2Clan.CP_CL_REGISTER_CREST)
		{
			if (clan.getLevel() < 3)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CLAN_LVL_3_NEEDED_TO_SET_CREST));
				return;
			}
			
			CrestCache crestCache = CrestCache.getInstance();
			
			int newId = IdFactory.getInstance().getNextId();
			
			if (clan.hasCrest())
			{
				crestCache.removePledgeCrest(newId);
			}
			
			if (!crestCache.savePledgeCrest(newId, _data))
			{
				_log.info( "Error loading crest of clan:" + clan.getName());
				return;
			}

            ClanRepository repository = DatabaseAccess.getRepository(ClanRepository.class);
            repository.updateClanCrestById(clan.getClanId(), newId);
			clan.setCrestId(newId);
			clan.setHasCrest(true);
			
			for (L2PcInstance member : clan.getOnlineMembers(""))
			{
				member.broadcastUserInfo();
			}
			
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.l2j.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__53_REQUESTSETPLEDGECREST;
	}
}
