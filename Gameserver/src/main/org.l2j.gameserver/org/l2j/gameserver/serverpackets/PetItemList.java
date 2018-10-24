package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetItemList extends L2GameServerPacket
{
	private static Logger _log = LoggerFactory.getLogger(PetItemList.class.getName());
	private static final String _S__cb_PETITEMLIST = "[S] b2  PetItemList";
	private final L2PetInstance _activeChar;
	
	public PetItemList(L2PetInstance character)
	{
		_activeChar = character;
		if (Config.DEBUG)
		{
			L2ItemInstance[] items = _activeChar.getInventory().getItems();
			for (L2ItemInstance temp : items)
			{
				_log.debug("item:" + temp.getItem().getName() + " type1:" + temp.getItem().getType1() + " type2:" + temp.getItem().getType2());
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xB2);
		
		L2ItemInstance[] items = _activeChar.getInventory().getItems();
		int count = items.length;
		writeShort(count);
		
		for (L2ItemInstance temp : items)
		{
			writeShort(temp.getItem().getType1().getId()); // item type1
			writeInt(temp.getObjectId());
			writeInt(temp.getItemId());
			writeInt(temp.getCount());
			writeShort(temp.getItem().getType2().getId()); // item type2
			writeShort(0xff); // ?
			if (temp.isEquipped())
			{
				writeShort(0x01);
			}
			else
			{
				writeShort(0x00);
			}
			writeInt(temp.getItem().getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			// writeShort(temp.getItem().getBodyPart()); // rev 377 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeShort(temp.getEnchantLevel()); // enchant level
			writeShort(0x00); // ?
		}
	}
}
