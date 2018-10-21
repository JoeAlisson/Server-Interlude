package org.l2j.gameserver.templates;

public enum BodyPart {
    NONE(0x0000),
    PENDANT(0x0001),
    RIGHT_EAR(0x0002),
    LEFT_EAR(0x0004),
    EAR(0X0006),
    NECK(0x0008),
    RIGHT_FINGER(0x0010),
    LEFT_FINGER(0x0020),
    FINGER(0x0030),
    HEAD(0x0040),
    RIGHT_HAND(0x0080),
    LEFT_HAND(0x0100),
    GLOVES(0x0200),
    CHEST(0x0400),
    LEGS(0x0800),
    FEET(0x1000),
    BACK(0x2000),
    TWO_HAND(0x4000),
    FULL_ARMOR(0x8000),
    HAIR(0x010000),
    FORMAL_WEAR(0x020000),
    DHAIR(0x040000),
    HAIRALL(0x080000),
    RIGHT_BRACELET(0x100000),
    LEFT_BRACELET(0x200000),
    DECO(0x0400000),
    SLOT_BELT(0x10000000),
    SLOT_BROOCH(0x20000000),
    SLOT_JEWEL(0x40000000),
    //  NOT USED todo REMOVE FROM DB
    UNDERWEAR(-1),
    WOLF(-1),
    HATCHLING(-1),
    STRIDER(-1),
    FACE(-1),
    BABYPET(-1)
    ;

    private final int id;

    BodyPart(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BodyPart fromId(int  id) {
        for (BodyPart bodyPart : values()) {
            if(bodyPart.getId() == id) {
                return bodyPart;
            }
        }
        return null;
    }
}
