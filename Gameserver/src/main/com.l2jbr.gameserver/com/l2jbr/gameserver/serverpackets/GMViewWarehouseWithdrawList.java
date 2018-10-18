package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import com.l2jbr.gameserver.model.entity.database.Weapon;

public class GMViewWarehouseWithdrawList extends L2GameServerPacket
{
	private final L2ItemInstance[] _items;
	private final String _playerName;
	private final L2PcInstance _activeChar;
	private final int _money;
	
	public GMViewWarehouseWithdrawList(L2PcInstance cha)
	{
		_activeChar = cha;
		_items = _activeChar.getWarehouse().getItems();
		_playerName = _activeChar.getName();
		_money = _activeChar.getAdena();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x95);
		writeString(_playerName);
		writeInt(_money);
		writeShort(_items.length);
		
		for (L2ItemInstance item : _items)
		{
			writeShort(item.getItem().getType1().getId());
			
			writeInt(item.getObjectId());
			writeInt(item.getItemId());
			writeInt(item.getCount());
			writeShort(item.getItem().getType2().getId());
			writeShort(item.getCustomType1());
			
			switch (item.getItem().getType2())
			{
				case TYPE2_WEAPON:
				{
					writeInt(item.getItem().getBodyPart().getId());
					writeShort(item.getEnchantLevel());
					writeShort(((Weapon) item.getItem()).getSoulshots());
					writeShort(((Weapon) item.getItem()).getSpiritshots());
					break;
				}
				
				case TYPE2_SHIELD_ARMOR:
				case TYPE2_ACCESSORY:
				case WEAR:
				case BRACELET:
				case TYPE2_PET_STRIDER:
				case TYPE2_PET_BABY:
				{
					writeInt(item.getItem().getBodyPart().getId());
					writeShort(item.getEnchantLevel());
					writeShort(0x00);
					writeShort(0x00);
					break;
				}
			}
			
			writeInt(item.getObjectId());
			
			switch (item.getItem().getType2())
			{
				case TYPE2_WEAPON:
				{
					if (item.isAugmented())
					{
						writeInt(0x0000FFFF & item.getAugmentation().getAugmentationId());
						writeInt(item.getAugmentation().getAugmentationId() >> 16);
					}
					else
					{
						writeInt(0);
						writeInt(0);
					}
					
					break;
				}
				
				case TYPE2_SHIELD_ARMOR:
				case TYPE2_ACCESSORY:
				case WEAR:
				case BRACELET:
				case TYPE2_PET_STRIDER:
				case TYPE2_PET_BABY:
				{
					writeInt(0);
					writeInt(0);
				}
			}
		}
	}
}
