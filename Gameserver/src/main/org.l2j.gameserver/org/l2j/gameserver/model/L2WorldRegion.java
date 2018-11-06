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

public final class L2WorldRegion {
    private static final Logger logger = LoggerFactory.getLogger(L2WorldRegion.class);

    private final Map<Integer, L2PlayableInstance> allPlayable;
    private final Map<Integer, L2Object> visibleObjects;
    private final List<L2WorldRegion> surroundingRegions;

    private final int _tileX, _tileY;
    private Boolean _active;
    private ScheduledFuture<?> _neighborsTask = null;

    private L2ZoneManager _zoneManager;

    public L2WorldRegion(int pTileX, int pTileY) {
        allPlayable = new ConcurrentHashMap<>();
        visibleObjects = new ConcurrentHashMap<>();
        surroundingRegions = new ArrayList<>();

        _tileX = pTileX;
        _tileY = pTileY;

        // default a newly initialized region to inactive, unless always on is specified
        if (Config.GRIDS_ALWAYS_ON) {
            _active = true;
        } else {
            _active = false;
        }
    }

    public void addZone(L2ZoneType zone) {
        if (_zoneManager == null) {
            _zoneManager = new L2ZoneManager();
        }
        _zoneManager.registerNewZone(zone);
    }

    public void removeZone(L2ZoneType zone) {
        if (_zoneManager == null) {
            return;
        }
        _zoneManager.unregisterZone(zone);
    }

    public void revalidateZones(L2Character character) {
        if (_zoneManager == null) {
            return;
        }

        if (_zoneManager != null) {
            _zoneManager.revalidateZones(character);
        }
    }

    public void removeFromZones(L2Character character) {
        if (_zoneManager == null) {
            return;
        }

        if (_zoneManager != null) {
            _zoneManager.removeCharacter(character);
        }
    }

    public void onDeath(L2Character character) {
        if (_zoneManager == null) {
            return;
        }

        if (_zoneManager != null) {
            _zoneManager.onDeath(character);
        }
    }

    public void onRevive(L2Character character) {
        if (_zoneManager == null) {
            return;
        }

        if (_zoneManager != null) {
            _zoneManager.onRevive(character);
        }
    }

    /**
     * Task of AI notification
     */
    public class NeighborsTask implements Runnable {
        private final boolean _isActivating;

        public NeighborsTask(boolean isActivating) {
            _isActivating = isActivating;
        }

        @Override
        public void run() {
            if (_isActivating) {
                // for each neighbor, if it's not active, activate.
                for (L2WorldRegion neighbor : getSurroundingRegions()) {
                    neighbor.setActive(true);
                }
            } else {
                if (areNeighborsEmpty()) {
                    setActive(false);
                }

                // check and deactivate
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

                    // Set target to null and cancel Attack or Cast
                    mob.setTarget(null);

                    // Stop movement
                    mob.stopMove(null);

                    // Stop allTemplates active skills effects in progress on the L2Character
                    mob.stopAllEffects();

                    mob.clearAggroList();
                    mob.getKnownList().removeAllKnownObjects();

                    mob.getAI().setIntention(org.l2j.gameserver.ai.Intention.AI_INTENTION_IDLE);

                    // stop the ai tasks
                    ((L2AttackableAI) mob.getAI()).stopAITask(false);

                    // Stop HP/MP/CP Regeneration task
                    // try this: allow regen, but only until mob is 100% full...then stop
                    // it until the grid is made active.
                    // mob.getStatus().stopHpMpRegeneration();
                }
            }
            logger.debug(c + " mobs were turned off");
        } else {
            for (L2Object o : visibleObjects.values()) {
                if (o instanceof L2Attackable) {
                    c++;
                    // Start HP/MP/CP Regeneration task
                    ((L2Attackable) o).getStatus().startHpMpRegeneration();

                    // start the ai
                    // ((L2AttackableAI) mob.getAI()).startAITask();
                } else if (o instanceof L2NpcInstance) {
                    // Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it
                    // L2Monsterinstance/L2Attackable socials are handled by AI (TODO: check the instances)
                    ((L2NpcInstance) o).startRandomAnimationTimer();
                }
            }
            logger.debug(c + " mobs were turned on");
        }

    }

    public Boolean isActive() {
        return _active;
    }

    // check if allTemplates 9 neighbors (including self) are inactive or active but with no players.
    // returns true if the above condition is met.
    public Boolean areNeighborsEmpty() {
        // if this region is occupied, return false.
        if (isActive() && (allPlayable.size() > 0)) {
            return false;
        }

        // if any one of the neighbors is occupied, return false
        for (L2WorldRegion neighbor : surroundingRegions) {
            if (neighbor.isActive() && (neighbor.allPlayable.size() > 0)) {
                return false;
            }
        }

        // in allTemplates other cases, return true.
        return true;
    }

    /**
     * this function turns this region's AI and geodata on or off
     *
     * @param value
     */
    public void setActive(boolean value) {
        if (_active == value) {
            return;
        }

        _active = value;

        // turn the AI on or off to match the region's activation.
        switchAI(value);

        // TODO
        // turn the geodata on or off to match the region's activation.
        if (value) {
            logger.debug("Starting Grid " + _tileX + "," + _tileY);
        } else {
            logger.debug("Stoping Grid " + _tileX + "," + _tileY);
        }
    }

    /**
     * Immediately sets self as active and starts a timer to set neighbors as active this timer is to avoid turning on neighbors in the case
     *  when a person just teleported into a region and then teleported out immediately...
     *  there is no reason to activate allTemplates the neighbors in that case.
     */
    private void startActivation() {
        // first set self to active and do self-tasks...
        setActive(true);

        // if the timer to deactivate neighbors is running, cancel it.
        if (nonNull(_neighborsTask )) {
            _neighborsTask.cancel(true);
            _neighborsTask = null;
        }

        // then, set a timer to activate the neighbors
        _neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(true), 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
    }

    /**
     * starts a timer to set neighbors (including self) as inactive this timer is to avoid turning off neighbors in the case when a person just moved out of a region that he may very soon return to. There is no reason to turn self & neighbors off in that case.
     */
    private void startDeactivation() {
        // if the timer to activate neighbors is running, cancel it.
        if (_neighborsTask != null) {
            _neighborsTask.cancel(true);
            _neighborsTask = null;
        }

        // start a timer to "suggest" a deactivate to self and neighbors.
        // suggest means: first check if a neighbor has L2PcInstances in it. If not, deactivate.
        _neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(false), 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
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

    /**
     * Remove the L2Object from the L2ObjectHashSet(L2Object) visibleObjects in this L2WorldRegion <BR>
     * <BR>
     * If L2Object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) allPlayable of this L2WorldRegion <BR>
     * Assert : object.getCurrentWorldRegion() == this || object.getCurrentWorldRegion() == null
     *
     * @param object
     */
    public void removeVisibleObject(L2Object object) {
        if (Config.ASSERT) {
            assert (object.getWorldRegion() == this) || (object.getWorldRegion() == null);
        }

        if (object == null) {
            return;
        }
        visibleObjects.remove(object);

        if (object instanceof L2PlayableInstance) {
            allPlayable.remove((L2PlayableInstance) object);

            if ((allPlayable.size() == 0) && (!Config.GRIDS_ALWAYS_ON)) {
                startDeactivation();
            }
        }
    }

    public void addSurroundingRegion(L2WorldRegion region) {
        surroundingRegions.add(region);
    }

    /**
     * Return the List surroundingRegions containing allTemplates L2WorldRegion around the current L2WorldRegion
     *
     * @return
     */
    public List<L2WorldRegion> getSurroundingRegions() {
        // change to return L2WorldRegion[] ?
        // this should not change after initialization, so maybe changes are not necessary

        return surroundingRegions;
    }

    public Iterator<L2PlayableInstance> iterateAllPlayers() {
        return allPlayable.values().iterator();
    }

    public Collection<L2Object> getVisibleObjects() {
        return visibleObjects.values();
    }

    public String getName() {
        return "(" + _tileX + ", " + _tileY + ")";
    }

    /**
     * Deleted allTemplates spawns in the world.
     */
    public synchronized void deleteVisibleNpcSpawns() {
        logger.debug("Deleting allTemplates visible NPC's in Region: {}", getName());
        for (L2Object obj : visibleObjects.values()) {
            if (obj instanceof L2NpcInstance) {
                L2NpcInstance target = (L2NpcInstance) obj;
                target.deleteMe();
                L2Spawn spawn = target.getSpawn();
                if (spawn != null) {
                    spawn.stopRespawn();
                    SpawnTable.getInstance().deleteSpawn(spawn, false);
                }
                logger.debug("Removed NPC " + target.getObjectId());
            }
        }
        logger.info("All visible NPC's deleted in Region: " + getName());
    }
}
