package org.l2j.gameserver.model.actor.knownlist;

import org.l2j.gameserver.model.L2Character;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class CharKnownList extends KnownList {

    public CharKnownList(L2Character activeChar) {
        super(activeChar);
    }

    // ###############################

    @Override
    public boolean addKnownObject(L2Object object) {
        return addKnownObject(object, null);
    }

    @Override
    public boolean addKnownObject(L2Object object, L2Character dropper) {
        if (!super.addKnownObject(object, dropper)) {
            return false;
        }
        if (object instanceof L2PcInstance) {
            getKnownPlayers().put(object.getObjectId(), (L2PcInstance) object);
            getKnownRelations().put(object.getObjectId(), -1);
        }
        return true;
    }

    /**
     * Return True if the L2PcInstance is in _knownPlayer of the L2Character.<BR>
     * <BR>
     *
     * @param player The L2PcInstance to search in _knownPlayer
     * @return
     */
    public final boolean knowsThePlayer(L2PcInstance player) {
        return (getActiveChar() == player) || getKnownPlayers().containsKey(player.getObjectId());
    }

    /**
     * Remove allTemplates L2Object from _knownObjects and _knownPlayer of the L2Character then cancel Attak or Cast and notify AI.
     */
    @Override
    public final void removeAllKnownObjects() {
        super.removeAllKnownObjects();
        getKnownPlayers().clear();
        getKnownRelations().clear();

        // Set _target of the L2Character to null
        // Cancel Attack or Cast
        getActiveChar().setTarget(null);

        // Cancel AI Task
        if (getActiveChar().hasAI()) {
            getActiveChar().setAI(null);
        }
    }

    @Override
    public boolean removeKnownObject(L2Object object) {
        if (!super.removeKnownObject(object)) {
            return false;
        }
        if (object instanceof L2PcInstance) {
            getKnownPlayers().remove(object.getObjectId());
            getKnownRelations().remove(object.getObjectId());
        }
        // If object is targeted by the L2Character, cancel Attack or Cast
        if (object == getActiveChar().getTarget()) {
            getActiveChar().setTarget(null);
        }

        return true;
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public
    public L2Character getActiveChar() {
        return (L2Character) super.getActiveObject();
    }

    @Override
    public int getDistanceToForgetObject(L2Object object) {
        return 0;
    }

    @Override
    public int getDistanceToWatchObject(L2Object object) {
        return 0;
    }
}
