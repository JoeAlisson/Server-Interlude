package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipUpdate extends L2GameServerPacket {

	private static Logger _log = LoggerFactory.getLogger(EquipUpdate.class.getName());
	
	private final L2ItemInstance _item;
	private final int _change;
	
	public EquipUpdate(L2ItemInstance item, int change)
	{
		_item = item;
		_change = change;
	}
	
	@Override
	protected final void writeImpl()
	{
		int bodypart = 0;
		writeByte(0x4b);
		writeInt(_change);
		writeInt(_item.getObjectId());

		switch (_item.getBodyPart())
		{
			case LEFT_EAR:
				bodypart = 0x01;
				break;
            case RIGHT_EAR:
				bodypart = 0x02;
				break;
			case NECK:
				bodypart = 0x03;
				break;
            case RIGHT_FINGER:
				bodypart = 0x04;
				break;
            case LEFT_FINGER:
				bodypart = 0x05;
				break;
			case HEAD:
				bodypart = 0x06;
				break;
            case RIGHT_HAND:
				bodypart = 0x07;
				break;
            case LEFT_HAND:
				bodypart = 0x08;
				break;
			case GLOVES:
				bodypart = 0x09;
				break;
			case CHEST:
				bodypart = 0x0a;
				break;
			case LEGS:
				bodypart = 0x0b;
				break;
			case FEET:
				bodypart = 0x0c;
				break;
			case BACK:
				bodypart = 0x0d;
				break;
            case TWO_HANDS:
				bodypart = 0x0e;
				break;
			case HAIR:
				bodypart = 0x0f;
				break;
		}
		
		if (Config.DEBUG)
		{
			_log.debug("body:" + bodypart);
		}
		writeInt(bodypart);
	}
}
