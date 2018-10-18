package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2ShortCut;
import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

/**
 * ShortCutInit format d *(1dddd)/(2ddddd)/(3dddd)
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShortCutInit extends L2GameServerPacket
{
	private L2ShortCut[] _shortCuts;
	private L2PcInstance _activeChar;
	
	public ShortCutInit(L2PcInstance activeChar)
	{
		_activeChar = activeChar;
		
		if (_activeChar == null)
		{
			return;
		}
		
		_shortCuts = _activeChar.getAllShortCuts();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x45);
		writeInt(_shortCuts.length);
		
		for (L2ShortCut sc : _shortCuts)
		{
			writeInt(sc.getType());
			writeInt(sc.getSlot() + (sc.getPage() * 12));
			
			switch (sc.getType())
			{
				case L2ShortCut.TYPE_ITEM: // 1
					writeInt(sc.getId());
					writeInt(0x01);
					writeInt(-1);
					writeInt(0x00);
					writeInt(0x00);
					writeShort(0x00);
					writeShort(0x00);
					break;
				case L2ShortCut.TYPE_SKILL: // 2
					writeInt(sc.getId());
					writeInt(sc.getLevel());
					writeByte(0x00); // C5
					writeInt(0x01); // C6
					break;
				case L2ShortCut.TYPE_ACTION: // 3
					writeInt(sc.getId());
					writeInt(0x01); // C6
					break;
				case L2ShortCut.TYPE_MACRO: // 4
					writeInt(sc.getId());
					writeInt(0x01); // C6
					break;
				case L2ShortCut.TYPE_RECIPE: // 5
					writeInt(sc.getId());
					writeInt(0x01); // C6
					break;
				default:
					writeInt(sc.getId());
					writeInt(0x01); // C6
			}
		}
	}
}
