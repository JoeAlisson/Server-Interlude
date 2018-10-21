package org.l2j.gameserver.serverpackets;

import java.util.Map;

/**
 * Format: (c) d[dS] d: list size [ d: char ID S: char Name ]
 * @author -Wooden-
 */
public class PackageToList extends L2GameServerPacket
{
	private final Map<Integer, String> _players;
	
	// Lecter : i put a char list here, but i'm unsure these really are Pc. I duno how freight work tho...
	public PackageToList(Map<Integer, String> players)
	{
		_players = players;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xC2);
		writeInt(_players.size());
		for (int objId : _players.keySet())
		{
			writeInt(objId); // you told me char id, i guess this was object id?
			writeString(_players.get(objId));
		}
	}
}