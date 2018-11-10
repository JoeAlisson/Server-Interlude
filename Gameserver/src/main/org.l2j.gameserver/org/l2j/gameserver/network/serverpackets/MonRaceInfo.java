package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2NpcInstance;

/**
 * sample 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000 - 0 damage (missed 0x80) 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10 3.... format dddc dddh (ddc)
 * @version $Revision: 1.1.6.2 $ $Date: 2005/03/27 15:29:39 $
 */
public class MonRaceInfo extends L2GameServerPacket
{
	private static final String _S__DD_MonRaceInfo = "[S] dd MonRaceInfo";
	private final int _unknown1;
	private final int _unknown2;
	private final L2NpcInstance[] _monsters;
	private final int[][] _speeds;
	
	public MonRaceInfo(int unknown1, int unknown2, L2NpcInstance[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}
	
	// 0xf3;;EtcStatusUpdatePacket;ddddd
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xdd);
		
		writeInt(_unknown1);
		writeInt(_unknown2);
		writeInt(8);
		
		for (int i = 0; i < 8; i++)
		{
			// System.out.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			writeInt(_monsters[i].getObjectId()); // npcObjectID
			writeInt(_monsters[i].getNpcId() + 1000000); // npcID
			writeInt(14107); // origin X
			writeInt(181875 + (58 * (7 - i))); // origin Y
			writeInt(-3566); // origin Z
			writeInt(12080); // end X
			writeInt(181875 + (58 * (7 - i))); // end Y
			writeInt(-3566); // end Z
			writeDouble(_monsters[i].getCollisionHeight()); // coll. height
			writeDouble(_monsters[i].getCollisionRadius()); // coll. radius
			writeInt(120); // ?? unknown
			// *
			for (int j = 0; j < 20; j++)
			{
				if (_unknown1 == 0)
				{
					writeByte(_speeds[i][j]);
				}
				else
				{
					writeByte(0);
				}
			}// */
			/*
			 * writeInt(0x77776666); writeInt(0x99998888); writeInt(0xBBBBAAAA); writeInt(0xDDDDCCCC); writeInt(0xFFFFEEEE); //
			 */
			writeInt(0);
		}
	}
}
