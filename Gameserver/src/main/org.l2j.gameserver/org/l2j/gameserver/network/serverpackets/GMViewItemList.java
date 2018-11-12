package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.Collection;

import static java.util.Objects.isNull;

public class GMViewItemList extends L2GameServerPacket
{
	private final Collection<L2ItemInstance> _items;
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
		writeInt(_cha.getInventoryLimit()); // inventory limit
		writeShort(0x01); // show window ??
		writeShort(_items.size());
		
		for (L2ItemInstance temp : _items)
		{
			if (isNull(temp))
			{
				continue;
			}
			
			writeShort(temp.getType().ordinal());
			
			writeInt(temp.getObjectId());
			writeInt(temp.getId());
			writeLong(temp.getCount());
			writeShort(temp.getCommissionType().ordinal());
			writeShort(temp.getCustomType1());
			writeShort(temp.isEquipped() ? 0x01 : 0x00);
			writeInt(0); // TODO temp.getItem().getBodyPart().getId());
			writeShort(temp.getEnchantLevel());
			writeShort(temp.getSubType());
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
