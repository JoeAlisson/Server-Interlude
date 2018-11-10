package org.l2j.gameserver.model.zone;

import org.l2j.gameserver.model.L2Character;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * This class manages allTemplates zones for a given world region
 * @author durgus
 */
public class L2ZoneManager {
	private final List<L2ZoneType> zones;

	public L2ZoneManager()
	{
		zones = new LinkedList<>();
	}

	public void registerNewZone(L2ZoneType zone) {
	    if(nonNull(zone)) {
            zones.add(zone);
        }
	}

	public void unregisterZone(L2ZoneType zone) {
		zones.remove(zone);
	}
	
	public void revalidateZones(L2Character character) {
		zones.forEach(zone -> zone.revalidateInZone(character));
	}
	
	public void removeCharacter(L2Character character) {
	    zones.forEach(zone -> zone.removeCharacter(character));
	}
	
	public void onDeath(L2Character character) {
	    zones.forEach(zone -> zone.onDieInside(character));
	}
	
	public void onRevive(L2Character character) {
	    zones.forEach(zone -> zone.onReviveInside(character));
	}
}
