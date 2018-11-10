package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.Henna;

import java.util.List;

public class HennaEquipList extends L2GameServerPacket
{
	private final L2PcInstance _player;
	private final List<Henna> _hennaEquipList;
	
	public HennaEquipList(L2PcInstance player, List<Henna> hennaEquipList)
	{
		_player = player;
		_hennaEquipList = hennaEquipList;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xe2);
		writeLong(_player.getAdena()); // activeChar current amount of aden
		writeInt(3); // available equip slot
		// writeInt(10); // total amount of symbol available which depends on difference classes
		writeInt(_hennaEquipList.size());
		
		for (Henna element : _hennaEquipList) {
			/*
			 * Player must have at least one dye in inventory to be able to see the henna that can be applied with it.
			 */
			if ((_player.getInventory().getItemByItemId(element.getDyeId())) != null)
			{
				writeInt(element.getSymbolId()); // symbolid
				writeInt(element.getDyeId()); // itemid of dye
				writeInt(element.getDyeAmount()); // amount of dye require
				writeInt(element.getPrice()); // amount of aden require
				writeInt(1); // meet the requirement or not
			}
			else
			{
				writeInt(0x00);
				writeInt(0x00);
				writeInt(0x00);
				writeInt(0x00);
				writeInt(0x00);
			}
		}
	}
}
