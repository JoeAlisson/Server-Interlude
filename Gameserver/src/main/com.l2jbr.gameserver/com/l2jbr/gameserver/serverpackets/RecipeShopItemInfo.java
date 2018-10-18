package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2World;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * ddddd
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopItemInfo extends L2GameServerPacket
{
	private final int _shopId;
	private final int _recipeId;
	
	public RecipeShopItemInfo(int shopId, int recipeId)
	{
		_shopId = shopId;
		_recipeId = recipeId;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (!(L2World.getInstance().findObject(_shopId) instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance manufacturer = (L2PcInstance) L2World.getInstance().findObject(_shopId);
		writeByte(0xda);
		writeInt(_shopId);
		writeInt(_recipeId);
		writeInt(manufacturer != null ? (int) manufacturer.getCurrentMp() : 0);
		writeInt(manufacturer != null ? manufacturer.getMaxMp() : 0);
		writeInt(0xffffffff);
	}
}
