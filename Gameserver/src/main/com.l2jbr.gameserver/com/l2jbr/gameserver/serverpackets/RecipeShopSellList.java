package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2ManufactureItem;
import com.l2jbr.gameserver.model.L2ManufactureList;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class RecipeShopSellList extends L2GameServerPacket
{
	private final L2PcInstance _buyer, _manufacturer;
	
	public RecipeShopSellList(L2PcInstance buyer, L2PcInstance manufacturer)
	{
		_buyer = buyer;
		_manufacturer = manufacturer;
	}
	
	@Override
	protected final void writeImpl()
	{
		L2ManufactureList createList = _manufacturer.getCreateList();
		
		if (createList != null)
		{
			// dddd d(ddd)
			writeByte(0xd9);
			writeInt(_manufacturer.getObjectId());
			writeInt((int) _manufacturer.getCurrentMp());// Creator's MP
			writeInt(_manufacturer.getMaxMp());// Creator's MP
			writeInt(_buyer.getAdena());// Buyer Adena
			
			int count = createList.size();
			writeInt(count);
			L2ManufactureItem temp;
			
			for (int i = 0; i < count; i++)
			{
				temp = createList.getList().get(i);
				writeInt(temp.getRecipeId());
				writeInt(0x00); // unknown
				writeInt(temp.getCost());
			}
		}
	}
}
