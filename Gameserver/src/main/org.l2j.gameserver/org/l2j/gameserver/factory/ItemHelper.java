package org.l2j.gameserver.factory;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.ItemLocation;
import org.l2j.gameserver.model.L2Augmentation;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.entity.database.ItemEntity;
import org.l2j.gameserver.model.entity.database.repository.AugmentationsRepository;
import org.l2j.gameserver.templates.base.PaperDoll;
import org.l2j.gameserver.templates.xml.jaxb.Armor;
import org.l2j.gameserver.templates.xml.jaxb.BodyPart;
import org.l2j.gameserver.templates.xml.jaxb.ItemTemplate;
import org.l2j.gameserver.templates.xml.jaxb.Weapon;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getRepository;

public class ItemHelper {

    private static final int SLOT_ONE_PIECE = 0x08000;
    private static final int SLOT_FULL_BODY = 0x20000;

    private static final int SUBTYPE_WEAPON = 0;
    private static final int SUBTYPE_ARMOR = 1;
    private static final int SUBTYPE_ACCESSORY = 2;
    private static final int SUBTYPE_PET = 3;
    private static final int SUBTYPE_CONSUMABLE = 4;
    private static final int SUBTYPE_OTHER = 5;

    public static L2ItemInstance create(int itemTemplateId) {
        var objectId = IdFactory.getInstance().getNextId();
        return new L2ItemInstance(objectId, itemTemplateId);
    }

    public static void releaseId(int objectId) {
        IdFactory.getInstance().releaseId(objectId);
    }

    public static L2ItemInstance load(ItemEntity itemEntity) {
        var template = ItemTable.getInstance().getTemplate(itemEntity.getItemId());
        L2ItemInstance item = new L2ItemInstance(template, itemEntity);
        var location = item.getLocation();
        var mana = item.getMana();

        if(mana == 0) {
            delete(item);
            return null;
        }

        if(mana > 0 && location == ItemLocation.PAPERDOLL) {
            item.scheduleConsumeManaTask();
        }

        var optAugmentation = getRepository(AugmentationsRepository.class).findById(item.getObjectId());
        optAugmentation.ifPresent(augmentation -> item.setAugmentation(new L2Augmentation(item, augmentation)));
        return item;
    }

    public static void delete(L2ItemInstance item) {
        if(isNull(item) || item.isWear()) {
            return;
        }

        if(item.isAugmented()) {
            getRepository(AugmentationsRepository.class).deleteById(item.getObjectId());
        }
        item.delete();
    }

    public static int getItemPaperDoll(ItemTemplate template) {
        var paperDoll = bodyPartToPaperdoll(template.getBodyPart());
        if (nonNull(paperDoll)) {
            return paperDoll.getMask();
        }
        return 0;
    }

    private static PaperDoll bodyPartToPaperdoll(BodyPart bodyPart) {
        PaperDoll paperdoll = null;
        switch (bodyPart) {
            case NONE:
                break;
            case EAR:
                paperdoll = PaperDoll.RIGHT_EAR;
                break;
            case ONE_PIECE:
                paperdoll = PaperDoll.CHEST;
                break;
            case FULL_BODY:
                paperdoll = PaperDoll.CHEST;
                break;
            case FULL_HAIR:
                paperdoll = PaperDoll.HAIR_DOWN;
                break;
            case DECO:
                paperdoll = PaperDoll.DECO1;
                break;
            case TALISMAN:
            case BROOCH_JEWEL:
                paperdoll = PaperDoll.JEWEL1;
                break;
            default:
                paperdoll = PaperDoll.valueOf(bodyPart.name());
                break;
        }
        return paperdoll;
    }

    public static int getItemSlot(ItemTemplate template) {
        return bodyPartToSlotId(template.getBodyPart());
    }

    private static int bodyPartToSlotId(BodyPart bodyPart) {
        if (isNull(bodyPart)) {
            return 0;
        }
        int slot = 0;
        switch (bodyPart) {
            case NONE:
                break;
            case ONE_PIECE:
                slot = SLOT_ONE_PIECE;
                break;
            case FULL_BODY:
                slot = SLOT_FULL_BODY;
                break;
            default:
                slot = bodyPartToPaperdoll(bodyPart).getSlotId();
        }
        return slot;
    }

    public static L2ItemInstance findFistsWeaponItem(int classId) {
        int itemId = 0;
        if ((classId >= 0x00) && (classId <= 0x09)) {
            // HUMAN FIGHTER fists
            itemId = 246;
        } else if ((classId >= 0x0a) && (classId <= 0x11)) {
            // HUMAN MAGE fists
            itemId = 251;
        } else if ((classId >= 0x12) && (classId <= 0x18)) {
            // elven FIGHTER fists
            itemId = 244;
        } else if ((classId >= 0x19) && (classId <= 0x1e)) {
            // elven MAGE fists
            itemId = 249;
        } else if ((classId >= 0x1f) && (classId <= 0x25)) {
            // dark elven FIGHTER fists
            itemId = 245;
        } else if ((classId >= 0x26) && (classId <= 0x2b)) {
            // dark elven MAGE fists
            itemId = 250;
        } else if ((classId >= 0x2c) && (classId <= 0x30)) {
            // ORC FIGHTER fists
            itemId = 248;
        } else if ((classId >= 0x31) && (classId <= 0x34)) {
            // ORC MAGE fists
            itemId = 252;
        } else if ((classId >= 0x35) && (classId <= 0x39)) {
            // dwarven fists
            itemId = 247;
        }
        return ItemTable.getInstance().createDummyItem(itemId);
    }

    public static int getSubType(ItemTemplate template) {
        if (template instanceof Weapon) {
            return SUBTYPE_WEAPON;
        }

        if (template instanceof Armor) {
            switch (template.getBodyPart()) {
                case BROOCH_JEWEL:
                case RIGHT_BRACELET:
                case LEFT_BRACELET:
                case RIGHT_FINGER:
                case LEFT_FINGER:
                case RIGHT_EAR:
                case LEFT_EAR:
                case TALISMAN:
                case EAR:
                case FINGER:
                case BROOCH:
                    return SUBTYPE_ACCESSORY;
                default:
                    return SUBTYPE_ARMOR;
            }
        }

        switch (template.getSubType()) {
            case PET_EQUIPMENT:
            case PET_SUPPLIES:
                return SUBTYPE_PET;
            case POTION:
            case HERB:
            case BOW:
            case SOULSHOT:
            case SCROLL_OTHER:
            case SPIRITSHOT:
            case SCROLL_ENCHANT_ARMOR:
            case SCROLL_ENCHANT_WEAPON:
                return SUBTYPE_CONSUMABLE;
        }
        return SUBTYPE_OTHER;
    }
}
