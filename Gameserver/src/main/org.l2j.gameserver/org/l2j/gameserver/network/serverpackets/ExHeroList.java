package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.entity.Heroes;
import org.l2j.gameserver.model.entity.database.Hero;

import java.util.Map;

/**
 * Format: (ch) d [SdSdSdd] d: size [ S: hero name d: hero class ID S: hero clan name d: hero clan crest id S: hero ally name d: hero Ally id d: count ]
 * @author -Wooden- Format from KenM Re-written by godson
 */
public class ExHeroList extends L2GameServerPacket
{
	private final Map<Integer, Hero> _heroList;
	
	public ExHeroList()
	{
		_heroList = Heroes.getInstance().getHeroes();
	}
	

	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x23);
		writeInt(_heroList.size());

        for (Hero hero: _heroList.values()) {
            writeString(hero.getCharName());
            writeInt(hero.getClassId());
            writeString(hero.getClanName());
            writeInt(hero.getclanCrest());
            writeString(hero.getAllyName());
            writeInt(hero.getAllyCrest());
            writeInt(hero.getCount());
        }
	}
}