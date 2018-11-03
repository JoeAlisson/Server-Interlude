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
package org.l2j.gameserver.handler.admincommandhandlers;

import org.l2j.commons.Config;
import org.l2j.gameserver.Announcements;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - announce text = announces text to allTemplates players - list_announcements = show menu - reload_announcements = reloads announcements from txt file - announce_announcements = announce allTemplates stored announcements to allTemplates players - add_announcement text = adds
 * text to startup announcements - del_announcement id = deletes announcement with respective id
 * @version $Revision: 1.4.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminAnnouncements implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_list_announcements",
		"admin_reload_announcements",
		"admin_announce_announcements",
		"admin_add_announcement",
		"admin_del_announcement",
		"admin_announce",
		"admin_announce_menu"
	};
	private static final int REQUIRED_LEVEL = Config.GM_ANNOUNCE;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
			{
				return false;
			}
		}

		Announcements announcements = Announcements.getInstance();
		if (command.equals("admin_list_announcements"))
		{
			announcements.listAnnouncements(activeChar);
		}
		else if (command.equals("admin_reload_announcements"))
		{
			announcements.loadAnnouncements();
			announcements.listAnnouncements(activeChar);
		}
		else if (command.startsWith("admin_announce_menu"))
		{
			announcements.handleAnnounce(command, 20);
			announcements.listAnnouncements(activeChar);
		}
		else if (command.equals("admin_announce_announcements"))
		{
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				announcements.showAnnouncements(player);
			}
			announcements.listAnnouncements(activeChar);
		}
		else if (command.startsWith("admin_add_announcement"))
		{
			// FIXME the reader can send only 16 chars (if you try to send more it sends null), remove this function or not?
			if (!command.equals("admin_add_announcement"))
			{
				try
				{
					String val = command.substring(23);
					announcements.addAnnouncement(val);
					announcements.listAnnouncements(activeChar);
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}// ignore errors
			}
		}
		else if (command.startsWith("admin_del_announcement"))
		{
			try
			{
				int val = Integer.parseInt(command.substring(23));
				announcements.delAnnouncement(val);
				announcements.listAnnouncements(activeChar);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		
		// Command is admin announce
		else if (command.startsWith("admin_announce"))
		{
			// Call method from another class
			announcements.handleAnnounce(command, 15);
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}
	
}
