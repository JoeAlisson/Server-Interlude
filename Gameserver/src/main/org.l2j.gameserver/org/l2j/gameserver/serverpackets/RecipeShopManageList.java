package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.L2ManufactureItem;
import org.l2j.gameserver.model.L2ManufactureList;
import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * dd d(dd) d(ddd)
 * @version $Revision: 1.1.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeShopManageList extends L2GameServerPacket
{
	private final L2PcInstance _seller;
	private final boolean _isDwarven;
	private L2RecipeList[] _recipes;
	
	public RecipeShopManageList(L2PcInstance seller, boolean isDwarven)
	{
		_seller = seller;
		_isDwarven = isDwarven;
		
		if (_isDwarven && _seller.hasDwarvenCraft())
		{
			_recipes = _seller.getDwarvenRecipeBook();
		}
		else
		{
			_recipes = _seller.getCommonRecipeBook();
		}
		
		// clean previous recipes
		if (_seller.getCreateList() != null)
		{
			L2ManufactureList list = _seller.getCreateList();
			for (L2ManufactureItem item : list.getList())
			{
				if (item.isDwarven() != _isDwarven)
				{
					list.getList().remove(item);
				}
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xd8);
		writeInt(_seller.getObjectId());
		writeInt(_seller.getAdena());
		writeInt(_isDwarven ? 0x00 : 0x01);
		
		if (_recipes == null)
		{
			writeInt(0);
		}
		else
		{
			writeInt(_recipes.length);// number of items in recipe book
			
			for (int i = 0; i < _recipes.length; i++)
			{
				L2RecipeList temp = _recipes[i];
				writeInt(temp.getId());
				writeInt(i + 1);
			}
		}
		
		if (_seller.getCreateList() == null)
		{
			writeInt(0);
		}
		else
		{
			L2ManufactureList list = _seller.getCreateList();
			writeInt(list.size());
			
			for (L2ManufactureItem item : list.getList())
			{
				writeInt(item.getRecipeId());
				writeInt(0x00);
				writeInt(item.getCost());
			}
		}
	}
}
