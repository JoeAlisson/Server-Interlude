package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.model.zone.type.L2FishingZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;


public class FishingZoneManager {

    private static Logger logger = LoggerFactory.getLogger(FishingZoneManager.class);
    private static FishingZoneManager _instance;
    private List<L2FishingZone> _fishingZones;


    public static final FishingZoneManager getInstance() {
        if (_instance == null) {
            logger.info("Initializing FishingZoneManager");
            _instance = new FishingZoneManager();
        }
        return _instance;
    }

    private FishingZoneManager() { }


    public void addFishingZone(L2FishingZone fishingZone) {
        if (_fishingZones == null) {
            _fishingZones = new LinkedList<>();
        }

        _fishingZones.add(fishingZone);
    }

    /*
     * isInsideFishingZone() - This function was modified to check the coordinates without caring for Z. This allows for the player to fish off bridges, into the water, or from other similar high places. One should be able to cast the line from up into the water, not only fishing whith one's feet
     * wet. :) TODO: Consider in the future, limiting the maximum height one can be above water, if we start getting "orbital fishing" players... xD
     */
    public final L2FishingZone isInsideFishingZone(int x, int y, int z) {
        for (L2FishingZone temp : _fishingZones) {
            if (temp.isInsideZone(x, y, temp.getWaterZ() - 10)) {
                return temp;
            }
        }
        return null;
    }
}
