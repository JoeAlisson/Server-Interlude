package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ShortCut;

/**
 * sample 56 01000000 04000000 dd9fb640 01000000 56 02000000 07000000 38000000 03000000 01000000 56 03000000 00000000 02000000 01000000 format dd d/dd/d d
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShortCutRegister extends L2GameServerPacket
{
	private final L2ShortCut _shortcut;

	public ShortCutRegister(L2ShortCut shortcut)
	{
		_shortcut = shortcut;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x44);
		
		writeInt(_shortcut.getType());
		writeInt(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
		switch (_shortcut.getType())
		{
			case L2ShortCut.TYPE_ITEM: // 1
				writeInt(_shortcut.getId());
				break;
			case L2ShortCut.TYPE_SKILL: // 2
				writeInt(_shortcut.getId());
				writeInt(_shortcut.getLevel());
				writeByte(0x00); // C5
				break;
			case L2ShortCut.TYPE_ACTION: // 3
				writeInt(_shortcut.getId());
				break;
			case L2ShortCut.TYPE_MACRO: // 4
				writeInt(_shortcut.getId());
				break;
			case L2ShortCut.TYPE_RECIPE: // 5
				writeInt(_shortcut.getId());
				break;
			default:
				writeInt(_shortcut.getId());
		}
		
		writeInt(1);// ??
	}
}
