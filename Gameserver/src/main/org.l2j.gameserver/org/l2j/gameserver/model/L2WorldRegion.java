package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.L2AttackableAI;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.actor.instance.L2NpcInstance;
import org.l2j.gameserver.model.actor.instance.L2PlayableInstance;
import org.l2j.gameserver.model.zone.L2ZoneManager;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.ai.Intention.AI_INTENTION_IDLE;

public final class L2WorldRegion {

    private static final Logger logger = LoggerFactory.getLogger(L2WorldRegion.class);

    private final Map<Integer, L2PlayableInstance> allPlayable;
    private final Map<Integer, L2Object> visibleObjects;
    private final List<L2WorldRegion> surroundingRegions;

    private final int tileX, tileY;
    private Boolean active;
    private ScheduledFuture<?> neighborsTask = null;

    private L2ZoneManager zoneManager;

    public L2WorldRegion(int pTileX, int pTileY) {
        allPlayable = new ConcurrentHashMap<>();
        visibleObjects = new ConcurrentHashMap<>();
        surroundingRegions = new ArrayList<>();

        tileX = pTileX;
        tileY = pTileY;

        active = Config.GRIDS_ALWAYS_ON;
    }

    public void addZone(L2ZoneType zone) {
        if (isNull(zoneManager)) {
            zoneManager = new L2ZoneManager();
        }
        zoneManager.registerNewZone(zone);
    }

    public void removeZone(L2ZoneType zone) {
        if (nonNull(zoneManager)) {
            zoneManager.unregisterZone(zone);
        }
    }

    public void revalidateZones(L2Character character) {
        if (nonNull(zoneManager)) {
            zoneManager.revalidateZones(character);
        }
    }

    public void removeFromZones(L2Character character) {
        if (nonNull(zoneManager )) {
            zoneManager.removeCharacter(character);
        }
    }

    public void onDeath(L2Character character) {
        if (nonNull(zoneManager)) {
            zoneManager.onDeath(character);
        }
    }

    public void onRevive(L2Character character) {
        if (nonNull(zoneManager)) {
            zoneManager.onRevive(character);
        }
    }

    public class NeighborsTask implements Runnable {
        private final boolean isActivating;

        public NeighborsTask(boolean isActivating) {
            this.isActivating = isActivating;
        }

        @Override
        public void run() {
            if (isActivating) {
                for (L2WorldRegion neighbor : getSurroundingRegions()) {
                    neighbor.setActive(true);
                }
            } else {
                if (areNeighborsEmpty()) {
                    setActive(false);
                }

                for (L2WorldRegion neighbor : getSurroundingRegions()) {
                    if (neighbor.areNeighborsEmpty()) {
                        neighbor.setActive(false);
                    }
                }
            }
        }
    }

    private void switchAI(Boolean isOn) {
        int c = 0;
        if (!isOn) {
            for (L2Object o : visibleObjects.values()) {
                if (o instanceof L2Attackable) {
                    c++;
                    L2Attackable mob = (L2Attackable) o;
                    mob.setTarget(null);
                    mob.stopMove(null);
                    mob.stopAllEffects();
                    mob.clearAggroList();
                    mob.getKnownList().removeAllKnownObjects();
                    mob.getAI().setIntention(AI_INTENTION_IDLE);
                    ((L2AttackableAI) mob.getAI()).stopAITask(false);
                }
            }
            logger.debug("{} mobs were turned off", c);
        } else {
            for (L2Object o : visibleObjects.values()) {
                if (o instanceof L2Attackable) {
                    c++;
                    ((L2Attackable) o).getStatus().startHpMpRegeneration();
                } else if (o instanceof L2NpcInstance) {
                    ((L2NpcInstance) o).startRandomAnimationTimer();
                }
            }
            logger.debug("{} mobs were turned on", c);
        }

    }

    public Boolean isActive() {
        return active;
    }

    public Boolean areNeighborsEmpty() {
        if (isActive() && (allPlayable.size() > 0)) {
            return false;
        }

        for (L2WorldRegion neighbor : surroundingRegions) {
            if (neighbor.isActive() && (neighbor.allPlayable.size() > 0)) {
                return false;
            }
        }
        return true;
    }

    public void setActive(boolean value) {
        if (active == value) {
            return;
        }

        active = value;
        switchAI(value);

        // TODO turn the geodata on or off to match the region's activation.
        if (value) {
            logger.debug("Starting Grid {}, {}", tileX, tileY);
        } else {
            logger.debug("Stoping Grid {}, {}", tileX, tileY);
        }
    }

    private void startActivation() {
        setActive(true);

        if (nonNull(neighborsTask)) {
            neighborsTask.cancel(true);
            neighborsTask = null;
        }

        neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(true), 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
    }

    private void startDeactivation() {
        if (nonNull(neighborsTask )) {
            neighborsTask.cancel(true);
            neighborsTask = null;
        }
        neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(false), 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
    }

    public void addVisibleObject(L2Object object) {

        if (isNull(object)) {
            return;
        }
        if(nonNull(visibleObjects.putIfAbsent(object.getObjectId(), object))) {
            logger.warn("objectId {} already exist in Region", object.getObjectId());
        }

        if (object instanceof L2PlayableInstance) {
            if(nonNull(allPlayable.put(object.getObjectId(), (L2PlayableInstance) object))) {
                logger.warn("objectId {} already exist in Region", object.getObjectId());
            }

            // if this is the first player to enter the region, activate self & neighbors
            if ((allPlayable.size() == 1) && (!Config.GRIDS_ALWAYS_ON)) {
                startActivation();
            }
        }
    }


    public void removeVisibleObject(L2Object object) {
        if (isNull(object)) {
            return;
        }
        visibleObjects.remove(object.getObjectId());

        if (object instanceof L2PlayableInstance) {
            allPlayable.remove( object.getObjectId());

            if ((allPlayable.size() == 0) && (!Config.GRIDS_ALWAYS_ON)) {
                startDeactivation();
            }
        }
    }

    public void addSurroundingRegion(L2WorldRegion region) {
        surroundingRegions.add(region);
    }

    public List<L2WorldRegion> getSurroundingRegions() {
        return surroundingRegions;
    }

    public Iterator<L2PlayableInstance> iterateAllPlayers() {
        return allPlayable.values().iterator();
    }

    public Collection<L2Object> getVisibleObjects() {
        return visibleObjects.values();
    }

    public String getName() {
        return String.format("(%s,%s)", tileX, tileY);
    }

    public synchronized void deleteVisibleNpcSpawns() {
        logger.debug("Deleting allTemplates visible NPC's in Region: {}", getName());
        for (L2Object obj : visibleObjects.values()) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance npc = (L2NpcInstance) obj;
                npc.deleteMe();
                L2Spawn spawn = npc.getSpawn();

                if (nonNull(spawn)) {
                    spawn.stopRespawn();
                    SpawnTable.getInstance().deleteSpawn(spawn, false);
                }

                logger.debug("Removed NPC {}",npc.getObjectId());
            }
        }
        logger.info("All visible NPC's deleted in Region: {}",  getName());
    }
}
