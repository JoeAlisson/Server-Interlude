package org.l2j.gameserver.templates.base;

public enum PaperDoll implements IUpdateTypeComponent {

    UNDERWEAR,
    RIGHT_EAR,
    LEFT_EAR,
    NECK,
    RIGHT_FINGER,
    LEFT_FINGER,
    HEAD,
    RIGHT_HAND,
    LEFT_HAND,
    GLOVES,
    CHEST,
    LEGS,
    FEET,
    CLOAK,
    TWO_HANDS,
    HAIR,
    HAIR_DOWN,
    RIGHT_BRACELET,
    LEFT_BRACELET,
    DECO1,
    DECO2,
    DECO3,
    DECO4,
    DECO5,
    DECO6,
    WAIST,
    BROOCH,
    JEWEL1,
    JEWEL2,
    JEWEL3,
    JEWEL4,
    JEWEL5,
    JEWEL6,
    ;

    @Override
    public int getMask() {
        return ordinal();
    }
}
