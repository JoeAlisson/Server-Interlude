package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.datatables.ItemTable;
import com.l2jbr.gameserver.model.L2Multisell.MultiSellEntry;
import com.l2jbr.gameserver.model.L2Multisell.MultiSellIngredient;
import com.l2jbr.gameserver.model.L2Multisell.MultiSellListContainer;

public class MultiSellList extends L2GameServerPacket
{
	private int _listId, _page, _finished;
	private MultiSellListContainer _list;
	
	public MultiSellList(MultiSellListContainer list, int page, int finished)
	{
		_list = list;
		_listId = list.getListId();
		_page = page;
		_finished = finished;
	}
	
	@Override
	protected void writeImpl()
	{
		// [ddddd] [dchh] [hdhdh] [hhdh]
		
		writeByte(0xd0);
		writeInt(_listId); // list id
		writeInt(_page); // page
		writeInt(_finished); // finished
		writeInt(0x28); // size of pages
		writeInt(_list == null ? 0 : _list.getEntries().size()); // list lenght
		
		if (_list != null)
		{
			for (MultiSellEntry ent : _list.getEntries())
			{
				writeInt(ent.getEntryId());
				writeInt(0x00); // C6
				writeInt(0x00); // C6
				writeByte(1);
				writeShort(ent.getProducts().size());
				writeShort(ent.getIngredients().size());
				
				for (MultiSellIngredient i : ent.getProducts())
				{
					writeShort(i.getItemId());
					writeInt(0);
					writeShort(ItemTable.getInstance().getTemplate(i.getItemId()).getType2().getId());
					writeInt(i.getItemCount());
					writeShort(i.getEnchantmentLevel()); // enchtant lvl
					writeInt(0x00); // C6
					writeInt(0x00); // C6
				}
				
				for (MultiSellIngredient i : ent.getIngredients())
				{
					int items = i.getItemId();
					int typeE = 65535;
					if (items != 65336)
					{
						typeE = ItemTable.getInstance().getTemplate(i.getItemId()).getType2().getId();
					}
					writeShort(items); // ID
					writeShort(typeE);
					writeInt(i.getItemCount()); // Count
					writeShort(i.getEnchantmentLevel()); // Enchant Level
					writeInt(0x00); // C6
					writeInt(0x00); // C6
				}
			}
		}
	}
}
