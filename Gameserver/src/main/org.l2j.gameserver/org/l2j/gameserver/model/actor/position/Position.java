package org.l2j.gameserver.model.actor.position;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.L2WorldRegion;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.util.Point3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

public class Position {
    private static final Logger logger = LoggerFactory.getLogger(Position.class);
    private static final int SAFE_DISTANCE = 500;

    private final L2Object activeObject;
    private L2WorldRegion worldRegion;

    private volatile int x;
    private volatile int y;
    private volatile int z;
    private int _heading = 0;



    public Position(L2Object activeObject) {
        this.activeObject = activeObject;
    }

    public final void setXYZ(int x, int y, int z) {
        synchronized (this) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        try {
            L2WorldRegion newRegion;
            if (activeObject.isVisible() && (newRegion = L2World.getInstance().getRegion(x, y)) != worldRegion ) {
                updateWorldRegion(newRegion);
            }
        } catch (Exception e) {
            logger.warn("Object Id at bad coords: (x: {}, y: {}, z {})", x, y, z);
            if (activeObject instanceof L2PcInstance) {
                ((L2PcInstance) activeObject).teleToLocation(0, 0, 0, false);
                ((L2PcInstance) activeObject).sendMessage("Error with your coords, Please ask a GM for help!");
            } else if (activeObject instanceof L2Character) {
                activeObject.decayMe();
            }
        }
    }

    public final void setSafeXYZ(int x, int y, int z) {
        if (x > L2World.MAP_MAX_X) {
            x = L2World.MAP_MAX_X - SAFE_DISTANCE;
        }
        if (x < L2World.MAP_MIN_X) {
            x = L2World.MAP_MIN_X + SAFE_DISTANCE;
        }
        if (y > L2World.MAP_MAX_Y) {
            y = L2World.MAP_MAX_Y - SAFE_DISTANCE;
        }
        if (y < L2World.MAP_MIN_Y) {
            y = L2World.MAP_MIN_Y + SAFE_DISTANCE;
        }

        setXYZ(x, y, z);
    }

    private void updateWorldRegion(L2WorldRegion newRegion) {
        if(nonNull(worldRegion)) {
            worldRegion.removeVisibleObject(activeObject);
        }

        worldRegion = newRegion;
        worldRegion.addVisibleObject(activeObject);
    }

    public final int getHeading() {
        return _heading;
    }

    public final void setHeading(int value) {
        _heading = value;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getZ() {
        return z;
    }

    public final L2WorldRegion getWorldRegion() {
        return worldRegion;
    }

    public final void setWorldRegion(L2WorldRegion value) {
        worldRegion = value;
    }
}
