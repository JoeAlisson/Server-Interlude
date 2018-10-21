package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * sample af 02 00 00 00 count 71 b3 70 4b object id 44 00 79 00 66 00 65 00 72 00 00 00 name 14 00 00 00 level 0f 00 00 00 class id 00 00 00 00 sex ?? 00 00 00 00 clan id 02 00 00 00 ?? 6f 5f 00 00 x af a9 00 00 y f7 f1 ff ff z c1 9c c0 4b object id 43 00 6a 00 6a 00 6a 00 6a 00 6f 00 6e 00 00 00
 * 0b 00 00 00 level 12 00 00 00 class id 00 00 00 00 sex ?? b1 01 00 00 clan id 00 00 00 00 13 af 00 00 38 b8 00 00 4d f4 ff ff * format d (dSdddddddd)
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class PartyMatchList extends L2GameServerPacket
{
	private final L2PcInstance[] _matchingPlayers;

	public PartyMatchList(L2PcInstance[] allPlayers)
	{
		_matchingPlayers = allPlayers;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x96);
		
		int size = _matchingPlayers.length;
		if (size > 40)
		{
			size = 40; // the client only displays 40 players, so we also limit the list to 40
		}
		
		writeInt(size);
		for (int i = 0; i < size; i++)
		{
			writeInt(_matchingPlayers[i].getObjectId());
			writeString(_matchingPlayers[i].getName());
			writeInt(_matchingPlayers[i].getLevel());
			writeInt(_matchingPlayers[i].getPlayerClass().getId());
			writeInt(00); // 00 -white name 01-red name
			writeInt(_matchingPlayers[i].getClanId());
			writeInt(00); // 00 - no affil 01-party 02-party pending 03-
			writeInt(_matchingPlayers[i].getX());
			writeInt(_matchingPlayers[i].getY());
			writeInt(_matchingPlayers[i].getZ());
		}
	}
}
