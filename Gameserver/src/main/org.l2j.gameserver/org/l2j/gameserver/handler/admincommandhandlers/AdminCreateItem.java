package org.l2j.gameserver.handler.admincommandhandlers;

import org.l2j.commons.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.GMAudit;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ItemList;
import org.l2j.gameserver.templates.xml.jaxb.Item;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;

import java.util.StringTokenizer;

/**
 * This class handles following admin commands: - itemcreate = show menu - create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_itemcreate",
		"admin_create_item"
	};
	private static final int REQUIRED_LEVEL = Config.GM_CREATE_ITEM;
	
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
		
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
		
		if (command.equals("admin_itemcreate"))
		{
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item"))
		{
			try
			{
				String val = command.substring(17);
				StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					String num = st.nextToken();
					int numval = Integer.parseInt(num);
					createItem(activeChar, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					createItem(activeChar, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //itemcreate <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("Specify a valid number.");
			}
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
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
	
	private void createItem(L2PcInstance activeChar, int id, int num)
	{
		if (num > 20)
		{
			ItemTemplate template = ItemTable.getInstance().getTemplate(id);
			if (!(template instanceof Item) || !((Item)template).isStackable())
			{
				activeChar.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
		}
		
		activeChar.getInventory().addItem("Admin", id, num, activeChar, null);
		
		ItemList il = new ItemList(activeChar, true);
		activeChar.sendPacket(il);
		
		activeChar.sendMessage("You have spawned " + num + " item(s) number " + id + " in your inventory.");
	}
}
