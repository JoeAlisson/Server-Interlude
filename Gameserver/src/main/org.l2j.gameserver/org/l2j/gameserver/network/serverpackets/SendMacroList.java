package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Macro;

/**
 * packet type id 0xe7 sample e7 d // unknown change of Macro edit,add,delete c // unknown c //count of Macros c // unknown d // id S // macro name S // desc S // acronym c // icon c // count c // entry c // type d // skill id c // shortcut id S // command name format: cdhcdSSScc (ccdcS)
 */
public class SendMacroList extends L2GameServerPacket
{
	private final int _rev;
	private final int _count;
	private final L2Macro _macro;
	
	public SendMacroList(int rev, int count, L2Macro macro)
	{
		_rev = rev;
		_count = count;
		_macro = macro;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xE7);
		
		writeInt(_rev); // macro change revision (changes after each macro edition)
		writeByte(0); // unknown
		writeByte(_count); // count of Macros
		writeByte(_macro != null ? 1 : 0); // unknown
		
		if (_macro != null)
		{
			writeInt(_macro.id); // Macro ID
			writeString(_macro.name); // Macro Name
			writeString(_macro.descr); // Desc
			writeString(_macro.acronym); // acronym
			writeByte(_macro.icon); // icon
			
			writeByte(_macro.commands.length); // count
			
			for (int i = 0; i < _macro.commands.length; i++)
			{
				L2Macro.L2MacroCmd cmd = _macro.commands[i];
				writeByte(i + 1); // i of count
				writeByte(cmd.type); // type 1 = skill, 3 = action, 4 = shortcut
				writeInt(cmd.d1); // skill id
				writeByte(cmd.d2); // shortcut id
				writeString(cmd.cmd); // command name
			}
		}
		
		// writeInt(1); //unknown change of Macro edit,add,delete
		// writeByte(0); //unknown
		// writeByte(1); //count of Macros
		// writeByte(1); //unknown
		//
		// writeInt(1430); //Macro ID
		// writeString("Admin"); //Macro Name
		// writeString("Admin Command"); //Desc
		// writeString("ADM"); //acronym
		// writeByte(0); //icon
		// writeByte(2); //count
		//
		// writeByte(1); //i of count
		// writeByte(3); //type 1 = skill, 3 = action, 4 = shortcut
		// writeInt(0); // skill id
		// writeByte(0); // shortcut id
		// writeString("/loc"); // command name
		//
		// writeByte(2); //i of count
		// writeByte(3); //type 1 = skill, 3 = action, 4 = shortcut
		// writeInt(0); // skill id
		// writeByte(0); // shortcut id
		// writeString("//admin"); // command name
	}
}
