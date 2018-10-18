package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.RecipeController;
import com.l2jbr.gameserver.model.L2RecipeList;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeItemMakeInfo extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(RecipeItemMakeInfo.class.getName());
	
	private final int _id;
	private final L2PcInstance _activeChar;
	private final boolean _success;
	
	public RecipeItemMakeInfo(int id, L2PcInstance player, boolean success)
	{
		_id = id;
		_activeChar = player;
		_success = success;
	}
	
	public RecipeItemMakeInfo(int id, L2PcInstance player)
	{
		_id = id;
		_activeChar = player;
		_success = true;
	}
	
	@Override
	protected final void writeImpl()
	{
		L2RecipeList recipe = RecipeController.getInstance().getRecipeById(_id);
		
		if (recipe != null)
		{
			writeByte(0xD7);
			
			writeInt(_id);
			writeInt(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
			writeInt((int) _activeChar.getCurrentMp());
			writeInt(_activeChar.getMaxMp());
			writeInt(_success ? 1 : 0); // item creation success/failed
		}
		else if (Config.DEBUG)
		{
			_log.info("No recipe found with ID = " + _id);
		}
	}
}
