package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.model.L2ItemInstance;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 0x42 WarehouseWithdrawalList dh (h dddhh dhhh d)
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class WareHouseWithdrawalList extends L2GameServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 4; // not sure
	private static Logger _log = LoggerFactory.getLogger(WareHouseWithdrawalList.class.getName());
	private L2PcInstance _activeChar;
	private int _playerAdena;
	private L2ItemInstance[] _items;
	private int _whType;
	
	public WareHouseWithdrawalList(L2PcInstance player, int type)
	{
		_activeChar = player;
		_whType = type;
		
		_playerAdena = _activeChar.getAdena();
		if (_activeChar.getActiveWarehouse() == null)
		{
			// Something went wrong!
			_log.warn("error while sending withdraw request to: " + _activeChar.getName());
			return;
		}
		_items = _activeChar.getActiveWarehouse().getItems();
		
		if (Config.DEBUG)
		{
			for (L2ItemInstance item : _items)
			{
				_log.debug("item:" + item.getItem().getName() + " type1:" + item.getItem().getType1() + " type2:" + item.getItem().getType2());
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x42);
		/*
		 * 0x01-Private Warehouse 0x02-Clan Warehouse 0x03-Castle Warehouse 0x04-Warehouse
		 */
		writeShort(_whType);
		writeInt(_playerAdena);
		writeShort(_items.length);
		
		for (L2ItemInstance item : _items)
		{
			writeShort(item.getItem().getType1().getId()); // item type1 //unconfirmed, works
			writeInt(0x00); // unconfirmed, works
			writeInt(item.getItemId()); // unconfirmed, works
			writeInt(item.getCount()); // unconfirmed, works
			writeShort(item.getItem().getType2().getId()); // item type2 //unconfirmed, works
			writeShort(0x00); // ?
			writeInt(item.getItem().getBodyPart().getId()); // ?
			writeShort(item.getEnchantLevel()); // enchant level -confirmed
			writeShort(0x00); // ?
			writeShort(0x00); // ?
			writeInt(item.getObjectId()); // item id - confimed
			if (item.isAugmented())
			{
				writeInt(0x0000FFFF & item.getAugmentation().getAugmentationId());
				writeInt(item.getAugmentation().getAugmentationId() >> 16);
			}
			else
			{
				writeLong(0x00);
			}
		}
	}

}
