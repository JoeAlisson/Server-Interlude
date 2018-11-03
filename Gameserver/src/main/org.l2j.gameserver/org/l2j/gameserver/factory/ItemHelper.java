package org.l2j.gameserver.factory;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.templates.xml.jaxb.BodyPart;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;

public class ItemHelper {

    private static final int SLOT_NONE = 0x00000;
    private static final int SLOT_UNDERWEAR = 0x00001;
    private static final int SLOT_RIGHT_EAR = 0x00002;
    private static final int SLOT_LEFT_EAR = 0x00004;
    private static final int SLOT_EAR = SLOT_RIGHT_EAR | SLOT_LEFT_EAR;
    private static final int SLOT_NECK = 0x00008;
    private static final int SLOT_RIGHT_FINGER = 0x00010;
    private static final int SLOT_LEFT_FINGER = 0x00020;
    private static final int SLOT_FINGER = SLOT_RIGHT_FINGER | SLOT_LEFT_FINGER;
    private static final int SLOT_HEAD = 0x00040;
    private static final int SLOT_RIGHT_HAND = 0x00080;
    private static final int SLOT_LEFT_HAND = 0x00100;
    private static final int SLOT_GLOVES = 0x00200;
    private static final int SLOT_CHEST = 0x00400;
    private static final int SLOT_LEGS = 0x00800;
    private static final int SLOT_FEET = 0x01000;
    private static final int SLOT_BACK = 0x02000;
    private static final int SLOT_TWO_HAND = 0x04000;
    private static final int SLOT_ONE_PIECE = 0x08000;
    private static final int SLOT_HAIR = 0x10000;
    private static final int SLOT_FULL_BODY = 0x20000;
    private static final int SLOT_HAIR_DOWN = 0x40000;
    private static final int SLOT_FULL_HAIR = 0x80000;
    private static final int SLOT_RIGHT_BRACELET = 0x100000;
    private static final int SLOT_LEFT_BRACELET = 0x200000;
    private static final int SLOT_DECO = 0x400000;
    private static final int SLOT_WAIST = 0x10000000;
    private static final int SLOT_BROOCH = 0x20000000;
    private static final int SLOT_JEWEL = 0x40000000;

    private static final int PAPERDOLL_UNDERWEAR = 0;
    private static final int PAPERDOLL_RIGHT_EAR = 1;
    private static final int PAPERDOLL_LEFT_EAR = 2;
    private static final int PAPERDOLL_NECK = 3;
    private static final int PAPERDOLL_RIGHT_FINGER = 4;
    private static final int PAPERDOLL_LEFT_FINGER = 5;
    private static final int PAPERDOLL_HEAD = 6;
    private static final int PAPERDOLL_RIGHT_HAND = 7;
    private static final int PAPERDOLL_LEFT_HAND = 8;
    private static final int PAPERDOLL_GLOVES = 9;
    private static final int PAPERDOLL_CHEST = 10;
    private static final int PAPERDOLL_LEGS = 11;
    private static final int PAPERDOLL_FEET = 12;
    private static final int PAPERDOLL_BACK = 13;
    private static final int PAPERDOLL_TWO_HANDS = 14;
    private static final int PAPERDOLL_HAIR = 15;
    private static final int PAPERDOLL_HAIR_DOWN = 16;
    private static final int PAPERDOLL_RIGHT_BRACELET = 17;
    private static final int PAPERDOLL_LEFT_BRACELET = 18;
    private static final int PAPERDOLL_DECO1 = 19;
    private static final int PAPERDOLL_DECO2 = 20;
    private static final int PAPERDOLL_DECO3 = 21;
    private static final int PAPERDOLL_DECO4 = 22;
    private static final int PAPERDOLL_DECO5 = 23;
    private static final int PAPERDOLL_DECO6 = 24;
    private static final int PAPERDOLL_WAIST = 25;
    private static final int PAPERDOLL_BROOCH = 26;
    private static final int PAPERDOLL_JEWEL1 = 27;
    private static final int PAPERDOLL_JEWEL2 = 28;
    private static final int PAPERDOLL_JEWEL3 = 29;
    private static final int PAPERDOLL_JEWEL4 = 30;
    private static final int PAPERDOLL_JEWEL5 = 31;
    private static final int PAPERDOLL_JEWEL6 = 32;

    public static L2ItemInstance create(int itemTemplateId) {
        var objectId = IdFactory.getInstance().getNextId();
        return new L2ItemInstance(objectId, itemTemplateId);
    }

    public static void releaseId(int objectId) {
        IdFactory.getInstance().releaseId(objectId);
    }

    public static int getItemSlot(ItemTemplate template) {
        return bodyPartToSlotId(template.getBodyPart());
    }

    public static int getItemPaperDoll(ItemTemplate template) {
        return bodyPartToPaperdollId(template.getBodyPart());
    }

    private static int bodyPartToPaperdollId(BodyPart bodyPart) {
        int paperdoll = -1;
        switch (bodyPart){
            case UNDERWEAR:
                paperdoll = PAPERDOLL_UNDERWEAR;
                break;
            case RIGHT_EAR:
                paperdoll = PAPERDOLL_RIGHT_EAR;
                break;
            case LEFT_EAR:
                paperdoll= PAPERDOLL_LEFT_EAR;
                break;
            case EAR:
                paperdoll = PAPERDOLL_RIGHT_EAR;
                break;
            case NECK:
                paperdoll = PAPERDOLL_NECK;
                break;
            case RIGHT_FINGER:
                paperdoll = PAPERDOLL_RIGHT_FINGER;
                break;
            case LEFT_FINGER:
                paperdoll = PAPERDOLL_LEFT_FINGER;
                break;
            case FINGER:
                paperdoll = PAPERDOLL_RIGHT_FINGER;
                break;
            case HEAD:
                paperdoll = PAPERDOLL_HEAD;
                break;
            case RIGHT_HAND:
                paperdoll = PAPERDOLL_RIGHT_HAND;
                break;
            case LEFT_HAND:
                paperdoll = PAPERDOLL_LEFT_HAND;
                break;
            case GLOVES:
                paperdoll =PAPERDOLL_GLOVES;
                break;
            case CHEST:
                paperdoll = PAPERDOLL_CHEST;
                break;
            case LEGS:
                paperdoll = PAPERDOLL_LEGS;
                break;
            case FEET:
                paperdoll = PAPERDOLL_FEET;
                break;
            case BACK:
                paperdoll = PAPERDOLL_BACK;
                break;
            case TWO_HANDS:
                paperdoll = PAPERDOLL_TWO_HANDS;
                break;
            case ONE_PIECE:
                paperdoll = PAPERDOLL_CHEST;
                break;
            case HAIR:
                paperdoll = PAPERDOLL_HAIR;
                break;
            case FULL_BODY:
                paperdoll = PAPERDOLL_CHEST;
                break;
            case HAIR_DOWN:
                paperdoll = PAPERDOLL_HAIR_DOWN;
                break;
            case FULL_HAIR:
                paperdoll = PAPERDOLL_HAIR;
                break;
            case RIGHT_BRACELET:
                paperdoll = PAPERDOLL_RIGHT_BRACELET;
                break;
            case LEFT_BRACELET:
                paperdoll = PAPERDOLL_LEFT_BRACELET;
                break;
            case DECO:
                paperdoll = PAPERDOLL_DECO1;
                break;
            case WAIST:
                paperdoll = PAPERDOLL_WAIST;
                break;
            case BROOCH:
                paperdoll = PAPERDOLL_BROOCH;
                break;
            case TALISMAN:
            case BROOCH_JEWEL:
                paperdoll = PAPERDOLL_JEWEL1;
                break;
        }
        return paperdoll;
    }

    private static int bodyPartToSlotId(BodyPart bodyPart) {
        int slot = 0;
        switch (bodyPart){
            case NONE:
                slot = SLOT_NONE;
                break;
            case UNDERWEAR:
                slot = SLOT_UNDERWEAR;
                break;
            case RIGHT_EAR:
                slot = SLOT_RIGHT_EAR;
                break;
            case LEFT_EAR:
                slot= SLOT_LEFT_EAR;
                break;
            case EAR:
                slot = SLOT_EAR;
                break;
            case NECK:
                slot = SLOT_NECK;
                break;
            case RIGHT_FINGER:
                slot = SLOT_RIGHT_FINGER;
                break;
            case LEFT_FINGER:
                slot = SLOT_LEFT_FINGER;
                break;
            case FINGER:
                slot = SLOT_FINGER;
                break;
            case HEAD:
                slot = SLOT_HEAD;
                break;
            case RIGHT_HAND:
                slot = SLOT_RIGHT_HAND;
                break;
            case LEFT_HAND:
                slot = SLOT_LEFT_HAND;
                break;
            case GLOVES:
                slot =SLOT_GLOVES;
                break;
            case CHEST:
                slot = SLOT_CHEST;
                break;
            case LEGS:
                slot = SLOT_LEGS;
                break;
            case FEET:
                slot = SLOT_FEET;
                break;
            case BACK:
                slot = SLOT_BACK;
                break;
            case TWO_HANDS:
                slot = SLOT_TWO_HAND;
                break;
            case ONE_PIECE:
                slot = SLOT_ONE_PIECE;
                break;
            case HAIR:
                slot = SLOT_HAIR;
                break;
            case FULL_BODY:
                slot = SLOT_FULL_BODY;
                break;
            case HAIR_DOWN:
                slot = SLOT_HAIR_DOWN;
                break;
            case FULL_HAIR:
                slot = SLOT_FULL_HAIR;
                break;
            case RIGHT_BRACELET:
                slot = SLOT_RIGHT_BRACELET;
                break;
            case LEFT_BRACELET:
                slot = SLOT_LEFT_BRACELET;
                break;
            case DECO:
                slot = SLOT_DECO;
                break;
            case WAIST:
                slot = SLOT_WAIST;
                break;
            case BROOCH:
                slot = SLOT_BROOCH;
                break;
            case TALISMAN:
            case BROOCH_JEWEL:
                slot = SLOT_JEWEL;
                break;
        }
        return slot;
    }

}
