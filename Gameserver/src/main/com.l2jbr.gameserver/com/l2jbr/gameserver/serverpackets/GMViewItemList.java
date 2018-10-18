package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class GMViewItemList extends L2GameServerPacket
{
	// private static Logger _log = LoggerFactory.getLogger(GMViewItemList.class.getName());
	private static final String _S__AD_GMVIEWITEMLIST = "[S] 94 GMViewItemList";
	private final L2ItemInstance[] _items;
	private final L2PcInstance _cha;
	private final String _playerName;
	
	public GMViewItemList(L2PcInstance cha)
	{
		_items = cha.getInventory().getItems();
		_playerName = cha.getName();
		_cha = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x94);
		writeString(_playerName);
		writeInt(_cha.GetInventoryLimit()); // inventory limit
		writeShort(0x01); // show window ??
		writeShort(_items.length);
		
		for (L2ItemInstance temp : _items)
		{
			if ((temp == null) || (temp.getItem() == null))
			{
				continue;
			}
			
			writeShort(temp.getItem().getType1().getId());
			
			writeInt(temp.getObjectId());
			writeInt(temp.getItemId());
			writeInt(temp.getCount());
			writeShort(temp.getItem().getType2().getId());
			writeShort(temp.getCustomType1());
			writeShort(temp.isEquipped() ? 0x01 : 0x00);
			writeInt(temp.getItem().getBodyPart().getId());
			writeShort(temp.getEnchantLevel());
			writeShort(temp.getCustomType2());
			if (temp.isAugmented())
			{
				writeInt(temp.getAugmentation().getAugmentationId());
			}
			else
			{
				writeInt(0x00);
			}
			writeInt(-1); // C6
		}
	}
}
