package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.network.SystemMessageId;

import java.util.Vector;

public class SystemMessage extends L2GameServerPacket
{
	// d d (d S/d d/d dd)
	// |--------------> 0 - String 1-number 2-textref npcname (1000000-1002655) 3-textref itemname 4-textref skills 5-??
	private static final int TYPE_ZONE_NAME = 7;
	private static final int TYPE_SKILL_NAME = 4;
	private static final int TYPE_ITEM_NAME = 3;
	private static final int TYPE_NPC_NAME = 2;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_TEXT = 0;
	private final int _messageId;
	private final Vector<Integer> _types = new Vector<>();
	private final Vector<Object> _values = new Vector<>();
	private int _skillLvL = 1;
	
	public SystemMessage(SystemMessageId messageId)
	{
		_messageId = messageId.getId();
	}
	
	@Deprecated
	public SystemMessage(int messageId)
	{
		_messageId = messageId;
	}
	
	public static SystemMessage sendString(String msg)
	{
		SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
		sm.addString(msg);
		
		return sm;
	}
	
	public SystemMessage addString(String text)
	{
		_types.add(TYPE_TEXT);
		_values.add(text);
		
		return this;
	}
	
	public SystemMessage addNumber(int number)
	{
		_types.add(TYPE_NUMBER);
		_values.add(number);
		return this;
	}
	
	public SystemMessage addNpcName(int id)
	{
		_types.add(TYPE_NPC_NAME);
		_values.add(1000000 + id);
		
		return this;
	}
	
	public SystemMessage addItemName(int id)
	{
		_types.add(TYPE_ITEM_NAME);
		_values.add(id);
		
		return this;
	}
	
	public SystemMessage addZoneName(int x, int y, int z)
	{
		_types.add(TYPE_ZONE_NAME);
		int[] coord =
		{
			x,
			y,
			z
		};
		_values.add(coord);
		
		return this;
	}
	
	public SystemMessage addSkillName(int id)
	{
		return addSkillName(id, 1);
	}
	
	public SystemMessage addSkillName(int id, int lvl)
	{
		_types.add(TYPE_SKILL_NAME);
		_values.add(id);
		_skillLvL = lvl;
		
		return this;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x62);
		
		writeShort(_messageId);
		writeByte(_types.size());
		
		for (int i = 0; i < _types.size(); i++)
		{
			int t = _types.get(i);
			
			writeByte(t);
			
			switch (t)
			{
				case TYPE_TEXT:
				{
					writeString((String) _values.get(i));
					break;
				}
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ITEM_NAME:
				{
					int t1 = (Integer) _values.get(i);
					writeInt(t1);
					break;
				}
				case TYPE_SKILL_NAME:
				{
					int t1 = (Integer) _values.get(i);
					writeInt(t1); // Skill Id
					writeInt(_skillLvL); // Skill lvl
					break;
				}
				case TYPE_ZONE_NAME:
				{
					int t1 = ((int[]) _values.get(i))[0];
					int t2 = ((int[]) _values.get(i))[1];
					int t3 = ((int[]) _values.get(i))[2];
					writeInt(t1);
					writeInt(t2);
					writeInt(t3);
					break;
				}
			}
		}
	}

	public int getMessageID()
	{
		return _messageId;
	}
}
