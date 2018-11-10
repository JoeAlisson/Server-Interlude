package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Sh (dd) h (dddd)
 * @author Tempy
 */
public class GMViewQuestList extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	
	public GMViewQuestList(L2PcInstance cha)
	{
		_activeChar = cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x93);
		writeString(_activeChar.getName());
		
		Quest[] questList = _activeChar.getAllActiveQuests();
		
		if (questList.length == 0)
		{
			writeByte(0);
			writeShort(0);
			writeShort(0);
			return;
		}
		
		writeShort(questList.length); // quest count
		
		for (Quest q : questList)
		{
			writeInt(q.getQuestIntId());
			
			QuestState qs = _activeChar.getQuestState(q.getName());
			
			if (qs == null)
			{
				writeInt(0);
				continue;
			}
			
			writeInt(qs.getInt("cond")); // stage of quest progress
		}
	}

}
