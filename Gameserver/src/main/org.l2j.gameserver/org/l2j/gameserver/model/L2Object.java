package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.knownlist.KnownList;
import org.l2j.gameserver.model.actor.position.Position;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Mother class of allTemplates objects in the world which ones is it possible to interact (PC, NPC, Item...)<BR>
 * <BR>
 * L2Object :<BR>
 * <BR>
 * <li>L2Character</li> <li>L2ItemInstance</li> <li>L2StaticObjectInstance</li>
 */
public abstract class L2Object {

    private final Position position;
    private boolean visible;
    private String polyMorphType;
    private int polyMorph;
    private String _name;

    protected KnownList knownList;
    protected final int objectId;

    public L2Object(int objectId) {
        this.objectId = objectId;
        position = new Position(this);
    }

    public KnownList getKnownList() {
        if (isNull(knownList)) {
            knownList = new KnownList(this);
        }
        return knownList;
    }

    public final int getObjectId() {
        return objectId;
    }

    public boolean isMorphed() {
        return nonNull(polyMorphType);
    }

    public int getPolyMorph() {
        return polyMorph;
    }

    public String getPolyMorphType() {
        return polyMorphType;
    }

    public void setPolyInfo(String type, String id) {
        polyMorphType = type;
        polyMorph = Integer.parseInt(id);
    }

    public String getName() {
        return _name;
    }

    public void onAction(L2PcInstance player) {
        player.sendPacket(new ActionFailed());
    }

    public void onActionShift(L2GameClient client) {
        client.getActiveChar().sendPacket(new ActionFailed());
    }

    public void onForcedAttack(L2PcInstance player) {
        player.sendPacket(new ActionFailed());
    }

    public void onSpawn() {
    }

    public final void setPosition(int x, int y, int z) {
        position.setXYZ(x, y, z);
    }

    public final void setPositionInvisible(int x, int y, int z) {
        visible = false;
        position.setSafeXYZ(x, y, z);
    }

    public final int getX() {
        return position.getX();
    }

    public final int getY() {
        return position.getY();
    }

    public final int getZ() {
        return position.getZ();
    }

    public void decayMe() {
        L2WorldRegion reg = position.getWorldRegion();

        synchronized (this) {
            visible = false;
            position.setWorldRegion(null);
        }
        L2World.getInstance().removeVisibleObject(this, reg);
        L2World.getInstance().removeObject(this);
        // TODO call onDecay here, no other way around
    }

    public final void setVisible(boolean value) {
        visible = value;
        if (!visible) {
            decayMe();
        }
    }

    public final void spawnMe() {
        spawnMe(getX(), getY(), getZ());
    }

    public final void spawnMe(int x, int y, int z) {

        synchronized (this) {
            // Set the x,y,z position of the L2Object spawn and update its world region
            visible = true;
            position.setSafeXYZ(x, y, z);

            // Add the L2Object spawn in the all objects of L2World
            L2World.getInstance().storeObject(this);
        }

        // this can synchronize on others instances, so it's out of synchronized, to avoid deadlocks
        // Add the L2Object spawn in the world as a visible object
        L2World.getInstance().addVisibleObject(this, position.getWorldRegion(), null);
        onSpawn();
    }

    public void setPosition(L2Object other) {
        if (isNull(other) || isNull(other.position)) {
            return;
        }
        var otherPosition = other.position;
        position.setXYZ(otherPosition.getX(), otherPosition.getY(), otherPosition.getZ());
    }

    public final void setKnownList(KnownList value) {
        knownList = value;
    }

    public final void setName(String value) {
        _name = value;
    }

    public final Position getPosition() {
        return position;
    }

    public final boolean isVisible() {
        return visible;
    }

    public L2WorldRegion getWorldRegion() {
        return position.getWorldRegion();
    }

    public boolean isAttackable() {
        return false;
    }

    public abstract boolean isAutoAttackable(L2Character attacker);

    @Override
    public String toString() {
        return String.valueOf(getObjectId());
    }
}