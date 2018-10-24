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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.SevenSigns;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.database.repository.CastleRepository;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

public class CastleManager {

    private static final Logger logger = LoggerFactory.getLogger(CastleManager.class);
    private static CastleManager _instance;

    public static final CastleManager getInstance() {
        if (isNull(_instance)) {
            logger.info("Initializing CastleManager");
            _instance = new CastleManager();
            _instance.load();
        }
        return _instance;
    }


    private List<Castle> _castles;

    private static final int _castleCirclets[] =
            {
                    0,
                    6838,
                    6835,
                    6839,
                    6837,
                    6840,
                    6834,
                    6836,
                    8182,
                    8183
            };

    private CastleManager() { }

    public final int findNearestCastleIndex(L2Object obj) {
        int index = getCastleIndex(obj);
        if (index < 0) {
            double closestDistance = 99999999;
            double distance;
            Castle castle;
            for (int i = 0; i < getCastles().size(); i++) {
                castle = getCastles().get(i);
                if (castle == null) {
                    continue;
                }
                distance = castle.getDistance(obj);
                if (closestDistance > distance) {
                    closestDistance = distance;
                    index = i;
                }
            }
        }
        return index;
    }

    private final void load() {
        CastleRepository repository = DatabaseAccess.getRepository(CastleRepository.class);
        repository.findAll().forEach(castle -> getCastles().add(new Castle(castle)));
    }

    public final Castle getCastleById(int castleId) {
        for (Castle temp : getCastles()) {
            if (temp.getCastleId() == castleId) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastleByOwner(L2Clan clan) {
        for (Castle temp : getCastles()) {
            if (temp.getOwnerId() == clan.getClanId()) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(String name) {
        for (Castle temp : getCastles()) {
            if (temp.getName().equalsIgnoreCase(name.trim())) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(int x, int y, int z) {
        for (Castle temp : getCastles()) {
            if (temp.checkIfInZone(x, y, z)) {
                return temp;
            }
        }
        return null;
    }

    public final Castle getCastle(L2Object activeObject) {
        return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
    }

    public final int getCastleIndex(int castleId) {
        Castle castle;
        for (int i = 0; i < getCastles().size(); i++) {
            castle = getCastles().get(i);
            if ((castle != null) && (castle.getCastleId() == castleId)) {
                return i;
            }
        }
        return -1;
    }

    public final int getCastleIndex(L2Object activeObject) {
        return getCastleIndex(activeObject.getX(), activeObject.getY(), activeObject.getZ());
    }

    public final int getCastleIndex(int x, int y, int z) {
        Castle castle;
        for (int i = 0; i < getCastles().size(); i++) {
            castle = getCastles().get(i);
            if ((castle != null) && castle.checkIfInZone(x, y, z)) {
                return i;
            }
        }
        return -1;
    }

    public final List<Castle> getCastles() {
        if (_castles == null) {
            _castles = new LinkedList<>();
        }
        return _castles;
    }

    public final void validateTaxes(int sealStrifeOwner) {
        int maxTax;
        switch (sealStrifeOwner) {
            case SevenSigns.CABAL_DUSK:
                maxTax = 5;
                break;
            case SevenSigns.CABAL_DAWN:
                maxTax = 25;
                break;
            default: // no owner
                maxTax = 15;
                break;
        }
        for (Castle castle : _castles) {
            if (castle.getTaxPercent() > maxTax) {
                castle.setTaxPercent(maxTax);
            }
        }
    }

    int _castleId = 1; // from this castle

    public int getCirclet() {
        return getCircletByCastleId(_castleId);
    }

    public int getCircletByCastleId(int castleId) {
        if ((castleId > 0) && (castleId < 10)) {
            return _castleCirclets[castleId];
        }

        return 0;
    }

    // remove this castle's circlets from the clan
    public void removeCirclet(L2Clan clan, int castleId) {
        for (L2ClanMember member : clan.getMembers()) {
            removeCirclet(member, castleId);
        }
    }

    public void removeCirclet(L2ClanMember member, int castleId) {
        if (member == null) {
            return;
        }
        L2PcInstance player = member.getPlayerInstance();
        int circletId = getCircletByCastleId(castleId);

        if (circletId != 0) {
            // online-player circlet removal
            if (player != null) {
                try {
                    L2ItemInstance circlet = player.getInventory().getItemByItemId(circletId);
                    if (circlet != null) {
                        if (circlet.isEquipped()) {
                            player.getInventory().unEquipItemInSlotAndRecord(circlet.getEquipSlot());
                        }
                        player.destroyItemByItemId("CastleCircletRemoval", circletId, 1, player, true);
                    }
                    return;
                } catch (NullPointerException e) {
                    // continue removing offline
                }
            }

            ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
            repository.deleteByOwnerAndItem(member.getObjectId(), circletId);
        }
    }
}
