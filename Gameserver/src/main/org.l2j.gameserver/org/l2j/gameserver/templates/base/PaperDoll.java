package org.l2j.gameserver.templates.base;

public enum PaperDoll implements IUpdateTypeComponent {

    UNDERWEAR(0x00001),
    RIGHT_EAR(0x00002),
    LEFT_EAR(0x00004),
    NECK(0x00008),
    RIGHT_FINGER(0x00010),
    LEFT_FINGER(0x00020),
    HEAD(0x00040),
    RIGHT_HAND(0x00080),
    LEFT_HAND(0x00100),
    GLOVES(0x00200),
    CHEST(0x00400),
    LEGS(0x00800),
    FEET(0x01000),
    CLOAK(0x02000),
    TWO_HANDS(0x04000),
    HAIR(0x10000),
    HAIR_DOWN(0x40000),
    RIGHT_BRACELET(0x100000),
    LEFT_BRACELET(0x200000),
    DECO1(0x400000),
    DECO2(0x400000),
    DECO3(0x400000),
    DECO4(0x400000),
    DECO5(0x400000),
    DECO6(0x400000),
    WAIST(0x10000000),
    BROOCH(0x20000000),
    JEWEL1(0x40000000),
    JEWEL2(0x40000000),
    JEWEL3(0x40000000),
    JEWEL4(0x40000000),
    JEWEL5(0x40000000),
    JEWEL6(0x40000000),
    ;

    private final int slotId;

    PaperDoll(int slotId) {
        this.slotId = slotId;
    }

    @Override
    public int getMask() {
        return ordinal();
    }

    public int getSlotId() {
        return slotId;
    }

}
