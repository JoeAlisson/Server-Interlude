/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.datatables.MapRegionTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.Zone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * A castle zone
 *
 * @author durgus
 */
public class L2CastleZone extends L2ZoneType {
    private int _castleId;
    private Castle _castle;
    private final int[] _spawnLoc;

    public L2CastleZone() {
        super();

        _spawnLoc = new int[3];
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("castleId")) {
            _castleId = Integer.parseInt(value);

            // Register self to the correct castle
            _castle = CastleManager.getInstance().getCastleById(_castleId);
            _castle.setZone(this);
        } else if (name.equals("spawnX")) {
            _spawnLoc[0] = Integer.parseInt(value);
        } else if (name.equals("spawnY")) {
            _spawnLoc[1] = Integer.parseInt(value);
        } else if (name.equals("spawnZ")) {
            _spawnLoc[2] = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(L2Character character) {
        if (_castle.getSiege().getIsInProgress()) {
            character.setInsideZone(Zone.PVP, true);
            character.setInsideZone(Zone.SIEGE, true);

            if (character instanceof L2PcInstance) {
                ((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
            }
        }
    }

    @Override
    protected void onExit(L2Character character) {
        if (_castle.getSiege().getIsInProgress()) {
            character.setInsideZone(Zone.PVP, false);
            character.setInsideZone(Zone.SIEGE, false);

            if (character instanceof L2PcInstance) {
                ((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));

                // Set pvp flag
                if (((L2PcInstance) character).getPvpFlag() == 0) {
                    ((L2PcInstance) character).startPvPFlag();
                }
            }
        }
        if (character instanceof L2SiegeSummonInstance) {
            ((L2SiegeSummonInstance) character).unSummon(((L2SiegeSummonInstance) character).getOwner());
        }
    }

    @Override
    protected void onDieInside(L2Character character) {
    }

    @Override
    protected void onReviveInside(L2Character character) {
    }

    public void updateZoneStatusForCharactersInside() {
        if (_castle.getSiege().getIsInProgress()) {
            for (L2Character character : characterList.values()) {
                try {
                    onEnter(character);
                } catch (NullPointerException e) {
                }
            }
        } else {
            for (L2Character character : characterList.values()) {
                try {
                    character.setInsideZone(Zone.PVP, false);
                    character.setInsideZone(Zone.SIEGE, false);

                    if (character instanceof L2PcInstance) {
                        ((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
                    }
                    if (character instanceof L2SiegeSummonInstance) {
                        ((L2SiegeSummonInstance) character).unSummon(((L2SiegeSummonInstance) character).getOwner());
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }

    /**
     * Removes allTemplates foreigners from the castle
     *
     * @param owningClanId
     */
    public void banishForeigners(int owningClanId) {
        for (L2Character temp : characterList.values()) {
            if (!(temp instanceof L2PcInstance)) {
                continue;
            }
            if (((L2PcInstance) temp).getClanId() == owningClanId) {
                continue;
            }

            ((L2PcInstance) temp).teleToLocation(MapRegionTable.TeleportWhereType.Town);
        }
    }

    /**
     * Sends a message to allTemplates players in this zone
     *
     * @param message
     */
    public void announceToPlayers(String message) {
        for (L2Character temp : characterList.values()) {
            if (temp instanceof L2PcInstance) {
                ((L2PcInstance) temp).sendMessage(message);
            }
        }
    }

    /**
     * Returns allTemplates players within this zone
     *
     * @return
     */
    public List<L2PcInstance> getAllPlayers() {
        List<L2PcInstance> players = new LinkedList<>();

        for (L2Character temp : characterList.values()) {
            if (temp instanceof L2PcInstance) {
                players.add((L2PcInstance) temp);
            }
        }

        return players;
    }

    /**
     * Get the castles defender spawn
     *
     * @return
     */
    public int[] getSpawn() {
        return _spawnLoc;
    }
}
