package org.l2j.gameserver.model.zone;

public enum Zone {
    PVP,
    PEACE,
    SIEGE,
    MOTHER_TREE,
    CLAN_HALL,
    UNUSED,
    NO_LANDING,
    WATER,
    JAIL,
    MONSTER_TRACK;

    public long getId() {
        return 1L << ordinal();
    }
}
