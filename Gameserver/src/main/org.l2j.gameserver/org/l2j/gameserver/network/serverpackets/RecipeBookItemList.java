package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2RecipeList;

/**
 * format d d(dd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeBookItemList extends L2GameServerPacket
{
	private L2RecipeList[] _recipes;
	private final boolean _isDwarvenCraft;
	private final int _maxMp;
	
	public RecipeBookItemList(boolean isDwarvenCraft, int maxMp)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_maxMp = maxMp;
	}
	
	public void addRecipes(L2RecipeList[] recipeBook)
	{
		_recipes = recipeBook;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xD6);
		
		writeInt(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
		writeInt(_maxMp);
		
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
	}
}
