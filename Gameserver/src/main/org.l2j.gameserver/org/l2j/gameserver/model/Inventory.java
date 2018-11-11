package org.l2j.gameserver.model;

import org.l2j.commons.Config;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.datatables.ArmorSetsTable;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.datatables.SkillTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.ArmorSet;
import org.l2j.gameserver.model.entity.database.repository.ItemRepository;
import org.l2j.gameserver.templates.base.PaperDoll;
import org.l2j.gameserver.templates.xml.jaxb.BodyPart;
import org.l2j.gameserver.templates.xml.jaxb.ItemType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.templates.xml.jaxb.BodyPart.*;

/**
 * This class manages inventory
 *
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer {

    private final L2ItemInstance[] _paperdoll;

    protected Inventory() {
        _paperdoll = new L2ItemInstance[PaperDoll.values().length];
        _paperdollListeners = new ArrayList<>(4);
        addPaperdollListener(new ArmorSetListener());
        addPaperdollListener(new BowListener());
        addPaperdollListener(new ItemPassiveSkillsListener());
        addPaperdollListener(new StatsListener());
    }

    public L2ItemInstance getPaperdollItem(PaperDoll paperDoll) {
        return _paperdoll[paperDoll.ordinal()];
    }
    // ===================================================

    public interface PaperdollListener {
        public void notifyEquiped(int slot, L2ItemInstance inst);

        public void notifyUnequiped(int slot, L2ItemInstance inst);
    }

    public static final int PAPERDOLL_PENDANT = 0;
    public static final int PAPERDOLL_REAR = 1;
    public static final int PAPERDOLL_LEAR = 2;
    public static final int PAPERDOLL_NECK = 3;
    public static final int PAPERDOLL_RFINGER = 4;
    public static final int PAPERDOLL_LFINGER = 5;
    public static final int PAPERDOLL_HEAD = 6;
    public static final int PAPERDOLL_RHAND = 7;
    public static final int PAPERDOLL_LHAND = 8;
    public static final int PAPERDOLL_GLOVES = 9;
    public static final int PAPERDOLL_CHEST = 10;
    public static final int PAPERDOLL_LEGS = 11;
    public static final int PAPERDOLL_FEET = 12;
    public static final int PAPERDOLL_BACK = 13;
    public static final int PAPERDOLL_LRHAND = 14;
    public static final int PAPERDOLL_HAIR = 15;
    public static final int PAPERDOLL_DHAIR = 16;
    public static final int PAPERDOLL_RBRACELET = 17;
    public static final int PAPERDOLL_LBRACELET = 18;
    public static final int PAPERDOLL_DECO1 = 19;
    public static final int PAPERDOLL_DECO2 = 20;
    public static final int PAPERDOLL_DECO3 = 21;
    public static final int PAPERDOLL_DECO4 = 22;
    public static final int PAPERDOLL_DECO5 = 23;
    public static final int PAPERDOLL_DECO6 = 24;
    public static final int PAPERDOLL_BELT = 25;
    public static final int PAPERDOLL_BROOCH = 26;
    public static final int PAPERDOLL_JEWEL1 = 27;
    public static final int PAPERDOLL_JEWEL2 = 28;
    public static final int PAPERDOLL_JEWEL3 = 29;
    public static final int PAPERDOLL_JEWEL4 = 30;
    public static final int PAPERDOLL_JEWEL5 = 31;
    public static final int PAPERDOLL_JEWEL6 = 32;

    // Speed percentage mods
    public static final double MAX_ARMOR_WEIGHT = 12000;
    private final List<PaperdollListener> _paperdollListeners;

    // protected to be accessed from child classes only
    protected int _totalWeight;

    // used to quickly check for using of items of special type
    private int _wearedMask;

    final class FormalWearListener implements PaperdollListener {
        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            if (!((getOwner() != null) && (getOwner() instanceof L2PcInstance))) {
                return;
            }

            L2PcInstance owner = (L2PcInstance) getOwner();

            if (item.getId() == 6408) {
                owner.setIsWearingFormalWear(false);
            }
        }

        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            if (!((getOwner() != null) && (getOwner() instanceof L2PcInstance))) {
                return;
            }

            L2PcInstance owner = (L2PcInstance) getOwner();

            // If reader equip Formal Wear unequip weapons and abort cast/attack
            if (item.getId() == 6408) {
                owner.setIsWearingFormalWear(true);
            } else {
                if (!owner.isWearingFormalWear()) {
                    return;
                }
            }
        }
    }

    /**
     * Recorder of alterations in inventory
     */
    public static final class ChangeRecorder implements PaperdollListener {
        private final Inventory _inventory;
        private final List<L2ItemInstance> _changed;

        /**
         * Constructor of the ChangeRecorder
         *
         * @param inventory
         */
        ChangeRecorder(Inventory inventory) {
            _inventory = inventory;
            _changed = new LinkedList<>();
            _inventory.addPaperdollListener(this);
        }

        /**
         * Add alteration in inventory when item equiped
         */
        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            if (!_changed.contains(item)) {
                _changed.add(item);
            }
        }

        /**
         * Add alteration in inventory when item unequiped
         */
        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            if (!_changed.contains(item)) {
                _changed.add(item);
            }
        }

        /**
         * Returns alterations in inventory
         *
         * @return L2ItemInstance[] : array of alterated items
         */
        public L2ItemInstance[] getChangedItems() {
            return _changed.toArray(new L2ItemInstance[_changed.size()]);
        }
    }

    final class BowListener implements PaperdollListener {
        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            if (slot != PAPERDOLL_LRHAND) {
                return;
            }
            if (Config.ASSERT) {
                assert null == getPaperdollItem(PAPERDOLL_LRHAND);
            }
            if (item.getType() == ItemType.BOW) {
                L2ItemInstance arrow = getPaperdollItem(PAPERDOLL_LHAND);
                if (arrow != null) {
                    setPaperdollItem(PAPERDOLL_LHAND, null);
                }
            }
        }

        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            if (slot != PAPERDOLL_LRHAND) {
                return;
            }
            if (Config.ASSERT) {
                assert item == getPaperdollItem(PAPERDOLL_LRHAND);
            }
            if (item.getType() == ItemType.BOW) {
                L2ItemInstance arrow = findArrowForBow(item);
                if (arrow != null) {
                    setPaperdollItem(PAPERDOLL_LHAND, arrow);
                }
            }
        }
    }

    final class StatsListener implements PaperdollListener {
        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            if (slot == PAPERDOLL_LRHAND) {
                return;
            }
            getOwner().removeStatsOwner(item);
        }

        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            if (slot == PAPERDOLL_LRHAND) {
                return;
            }
            getOwner().addStatFuncs(item.getStatFuncs(getOwner()));
        }
    }

    final class ItemPassiveSkillsListener implements PaperdollListener {
        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            L2PcInstance player;

            if (getOwner() instanceof L2PcInstance) {
                player = (L2PcInstance) getOwner();
            } else {
                return;
            }

            L2Skill passiveSkill = null;


            if (item.isWeapon()) {
                // passiveSkill = ((Weapon) it).getSkill(); TODO implement
            } else if (item.isArmor()) {
                // passiveSkill = ((Armor) it).getSkill(); TODO implement
            }

            if (passiveSkill != null) {
                player.removeSkill(passiveSkill, false);
                player.sendSkillList();
            }
        }

        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            L2PcInstance player;

            if (getOwner() instanceof L2PcInstance) {
                player = (L2PcInstance) getOwner();
            } else {
                return;
            }

            L2Skill passiveSkill = null;

            if (item.isWeapon()) {
                // passiveSkill = ((Weapon) it).getSkill(); TODO IMPLEMENT

            } else if (item.isArmor()) {
                // passiveSkill = ((Armor) it).getSkill(); TODO IMPLEMENT
            }

            if (passiveSkill != null) {
                player.addSkill(passiveSkill, false);
                player.sendSkillList();
            }
        }
    }

    final class ArmorSetListener implements PaperdollListener {
        @Override
        public void notifyEquiped(int slot, L2ItemInstance item) {
            if (!(getOwner() instanceof L2PcInstance)) {
                return;
            }

            L2PcInstance player = (L2PcInstance) getOwner();

            // checks if reader worns chest item
            L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST);
            if (chestItem == null) {
                return;
            }

            // checks if there is armorset for chest item that reader worns
            ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getId());
            if (armorSet == null) {
                return;
            }

            // checks if equiped item is part of set
            if (armorSet.containItem(slot, item.getId())) {
                if (armorSet.containAll(player)) {
                    L2Skill skill = SkillTable.getInstance().getInfo(armorSet.getSkillId(), 1);
                    if (skill != null) {
                        player.addSkill(skill, false);
                        player.sendSkillList();
                    } else {
                        _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getSkillId() + ".");
                    }

                    if (armorSet.containShield(player)) // has shield from set
                    {
                        L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
                        if (skills != null) {
                            player.addSkill(skills, false);
                            player.sendSkillList();
                        } else {
                            _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
                        }
                    }
                    if (armorSet.isEnchanted6(player)) // has allTemplates parts of set enchanted to 6 or more
                    {
                        int skillId = armorSet.getEnchant6Skill();
                        if (skillId > 0) {
                            L2Skill skille = SkillTable.getInstance().getInfo(skillId, 1);
                            if (skille != null) {
                                player.addSkill(skille, false);
                                player.sendSkillList();
                            } else {
                                _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getEnchant6Skill() + ".");
                            }
                        }
                    }
                }
            } else if (armorSet.containShield(item.getId())) {
                if (armorSet.containAll(player)) {
                    L2Skill skills = SkillTable.getInstance().getInfo(armorSet.getShieldSkillId(), 1);
                    if (skills != null) {
                        player.addSkill(skills, false);
                        player.sendSkillList();
                    } else {
                        _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + armorSet.getShieldSkillId() + ".");
                    }
                }
            }
        }

        @Override
        public void notifyUnequiped(int slot, L2ItemInstance item) {
            if (!(getOwner() instanceof L2PcInstance)) {
                return;
            }

            L2PcInstance player = (L2PcInstance) getOwner();

            boolean remove = false;
            int removeSkillId1 = 0; // set skill
            int removeSkillId2 = 0; // shield skill
            int removeSkillId3 = 0; // enchant +6 skill

            if (slot == PAPERDOLL_CHEST) {
                ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(item.getId());
                if (armorSet == null) {
                    return;
                }

                remove = true;
                removeSkillId1 = armorSet.getSkillId();
                removeSkillId2 = armorSet.getShieldSkillId();
                removeSkillId3 = armorSet.getEnchant6Skill();
            } else {
                L2ItemInstance chestItem = getPaperdollItem(PAPERDOLL_CHEST);
                if (chestItem == null) {
                    return;
                }

                ArmorSet armorSet = ArmorSetsTable.getInstance().getSet(chestItem.getId());
                if (armorSet == null) {
                    return;
                }

                if (armorSet.containItem(slot, item.getId())) // removed part of set
                {
                    remove = true;
                    removeSkillId1 = armorSet.getSkillId();
                    removeSkillId2 = armorSet.getShieldSkillId();
                    removeSkillId3 = armorSet.getEnchant6Skill();
                } else if (armorSet.containShield(item.getId())) // removed shield
                {
                    remove = true;
                    removeSkillId2 = armorSet.getShieldSkillId();
                }
            }

            if (remove) {
                if (removeSkillId1 != 0) {
                    L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId1, 1);
                    if (skill != null) {
                        player.removeSkill(skill);
                    } else {
                        _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId1 + ".");
                    }
                }
                if (removeSkillId2 != 0) {
                    L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId2, 1);
                    if (skill != null) {
                        player.removeSkill(skill);
                    } else {
                        _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId2 + ".");
                    }
                }
                if (removeSkillId3 != 0) {
                    L2Skill skill = SkillTable.getInstance().getInfo(removeSkillId3, 1);
                    if (skill != null) {
                        player.removeSkill(skill);
                    } else {
                        _log.warn("Inventory.ArmorSetListener: Incorrect skill: " + removeSkillId3 + ".");
                    }
                }
                player.sendSkillList();
            }
        }
    }

    /*
     * final class FormalWearListener implements PaperdollListener { public void notifyUnequiped(int slot, L2ItemInstance item) { if (!(getOwner() != null && getOwner() instanceof L2PcInstance)) return; L2PcInstance owner = (L2PcInstance)getOwner(); if (item.getId() == 6408)
     * owner.setIsWearingFormalWear(false); } public void notifyEquiped(int slot, L2ItemInstance item) { if (!(getOwner() != null && getOwner() instanceof L2PcInstance)) return; L2PcInstance owner = (L2PcInstance)getOwner(); // If reader equip Formal Wear unequip weapons and abort cast/attack if
     * (item.getId() == 6408) { owner.setIsWearingFormalWear(true); if (owner.isCastingNow()) owner.abortCast(); if (owner.isAttackingNow()) owner.abortAttack(); setPaperdollItem(PAPERDOLL_LHAND, null); setPaperdollItem(PAPERDOLL_RHAND, null); setPaperdollItem(PAPERDOLL_LRHAND, null); } else {
     * if (!owner.isWearingFormalWear()) return; // Don't let weapons be equipped if reader is wearing Formal Wear if (slot == PAPERDOLL_LHAND || slot == PAPERDOLL_RHAND || slot == PAPERDOLL_LRHAND) { setPaperdollItem(slot, null); } } } }
     */

    /**
     * Constructor of the inventory
     */


    protected abstract ItemLocation getEquipLocation();

    /**
     * Returns the instance of new ChangeRecorder
     *
     * @return ChangeRecorder
     */
    public ChangeRecorder newRecorder() {
        return new ChangeRecorder(this);
    }

    /**
     * Drop item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : L2ItemInstance to be dropped
     * @param actor     : L2PcInstance Player requesting the item drop
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance dropItem(String process, L2ItemInstance item, L2PcInstance actor, L2Object reference) {
        synchronized (item) {
            if (!_items.contains(item)) {
                return null;
            }

            removeItem(item);
            item.setOwnerId(process, 0);
            item.setLocation(ItemLocation.VOID);
            item.setLastChange(L2ItemInstance.REMOVED);

            item.updateDatabase();
            refreshWeight();
        }
        return item;
    }

    /**
     * Drop item from inventory by using its <B>objectID</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be dropped
     * @param count     : int Quantity of items to be dropped
     * @param actor     : L2PcInstance Player requesting the item drop
     * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the destroyed item or the updated item in inventory
     */
    public L2ItemInstance dropItem(String process, int objectId, long count, L2PcInstance actor, L2Object reference) {
        L2ItemInstance item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        // Adjust item quantity and create new instance to drop
        if (item.getCount() > count) {
            item.changeCount(process, -count);
            item.setLastChange(L2ItemInstance.MODIFIED);
            item.updateDatabase();

            item = ItemTable.getInstance().createItem(process, item.getId(), count, actor, reference);

            item.updateDatabase();
            refreshWeight();
            return item;
        }
        // Directly drop entire item
        return dropItem(process, item, actor, reference);
    }

    /**
     * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)<BR>
     * <BR>
     *
     * @param item : L2ItemInstance to be added from inventory
     */
    @Override
    protected void addItem(L2ItemInstance item) {
        super.addItem(item);
        if (item.isEquipped()) {
            equipItem(item);
        }
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param item : L2ItemInstance to be removed from inventory
     */
    @Override
    protected void removeItem(L2ItemInstance item) {
        // Unequip item if equiped
        // if (item.isEquipped()) unEquipItemInSlotAndRecord(item.getEquipSlot());
        for (int i = 0; i < _paperdoll.length; i++) {
            if (_paperdoll[i] == item) {
                unEquipItemInSlot(i);
            }
        }

        super.removeItem(item);
    }

    /**
     * Returns the item in the paperdoll slot
     *
     * @param slot
     * @return L2ItemInstance
     */
    public L2ItemInstance getPaperdollItem(int slot) {
        return _paperdoll[slot];
    }

    /**
     * Returns the item in the paperdoll ItemTemplate slot
     *
     * @param slot
     * @return L2ItemInstance
     */
    public L2ItemInstance getPaperdollItemByL2ItemId(BodyPart slot) {
        // TODO use bodypart
        switch (slot.ordinal()) {
            case 0x01:
                return _paperdoll[0];
            case 0x04:
                return _paperdoll[1];
            case 0x02:
                return _paperdoll[2];
            case 0x08:
                return _paperdoll[3];
            case 0x20:
                return _paperdoll[4];
            case 0x10:
                return _paperdoll[5];
            case 0x40:
                return _paperdoll[6];
            case 0x80:
                return _paperdoll[7];
            case 0x0100:
                return _paperdoll[8];
            case 0x0200:
                return _paperdoll[9];
            case 0x0400:
                return _paperdoll[10];
            case 0x0800:
                return _paperdoll[11];
            case 0x1000:
                return _paperdoll[12];
            case 0x2000:
                return _paperdoll[13];
            case 0x4000:
                return _paperdoll[14];
            case 0x040000:
                return _paperdoll[15];
            case 0x010000:
                return _paperdoll[16];
            case 0x080000:
                return _paperdoll[17];
        }
        return null;
    }

    /**
     * Returns the ID of the item in the paperdol slot
     *
     * @param slot : int designating the slot
     * @return int designating the ID of the item
     */
    public int getPaperdollItemId(int slot) {
        L2ItemInstance item = _paperdoll[slot];
        if (item != null) {
            return item.getId();
        } else if (slot == PAPERDOLL_HAIR) {
            item = _paperdoll[PAPERDOLL_DHAIR];
            if (item != null) {
                return item.getId();
            }
        }
        return 0;
    }

    public int getPaperdollAugmentationId(int slot) {
        L2ItemInstance item = _paperdoll[slot];
        if ((item != null) && (item.getAugmentation() != null)) {
            return item.getAugmentation().getAugmentationId();
        }
        return 0;
    }

    /**
     * Returns the objectID associated to the item in the paperdoll slot
     *
     * @param slot : int pointing out the slot
     * @return int designating the objectID
     */
    public int getPaperdollObjectId(int slot) {
        L2ItemInstance item = _paperdoll[slot];
        if (item != null) {
            return item.getObjectId();
        } else if (slot == PAPERDOLL_HAIR) {
            item = _paperdoll[PAPERDOLL_DHAIR];
            if (item != null) {
                return item.getObjectId();
            }
        }
        return 0;
    }

    /**
     * Adds new inventory's paperdoll listener
     *
     * @param listener
     */
    public synchronized void addPaperdollListener(PaperdollListener listener) {
        if (Config.ASSERT) {
            assert !_paperdollListeners.contains(listener);
        }
        _paperdollListeners.add(listener);
    }

    /**
     * Removes a paperdoll listener
     *
     * @param listener
     */
    public synchronized void removePaperdollListener(PaperdollListener listener) {
        _paperdollListeners.remove(listener);
    }

    /**
     * Equips an item in the given slot of the paperdoll. <U><I>Remark :</I></U> The item <B>HAS TO BE</B> already in the inventory
     *
     * @param slot : int pointing out the slot of the paperdoll
     * @param item : L2ItemInstance pointing out the item to add in slot
     * @return L2ItemInstance designating the item placed in the slot before
     */
    public L2ItemInstance setPaperdollItem(int slot, L2ItemInstance item) {
        L2ItemInstance old = _paperdoll[slot];
        if (old != item) {
            if (old != null) {
                _paperdoll[slot] = null;
                // Put old item from paperdoll slot to base location
                old.setLocation(getBaseLocation());
                old.setLastChange(L2ItemInstance.MODIFIED);
                // Get the mask for paperdoll
                int mask = 0;
                for (int i = 0; i < PAPERDOLL_LRHAND; i++) {
                    L2ItemInstance pi = _paperdoll[i];
                    if (pi != null) {
                        mask |= 1 << pi.getType().ordinal();
                    }
                }
                _wearedMask = mask;
                // Notify allTemplates paperdoll listener in order to unequip old item in slot
                for (PaperdollListener listener : _paperdollListeners) {
                    if (listener == null) {
                        continue;
                    }
                    listener.notifyUnequiped(slot, old);
                }
                old.updateDatabase();
            }
            // Add new item in slot of paperdoll
            if (item != null) {
                _paperdoll[slot] = item;
                item.setLocation(getEquipLocation(), slot);
                item.setLastChange(L2ItemInstance.MODIFIED);
                _wearedMask |= 1 << item.getType().ordinal();
                for (PaperdollListener listener : _paperdollListeners) {
                    listener.notifyEquiped(slot, item);
                }
                item.updateDatabase();
            }
        }
        return old;
    }

    /**
     * Return the mask of weared item
     *
     * @return int
     */
    public int getWearedMask() {
        return _wearedMask;
    }

    public BodyPart getSlotFromItem(L2ItemInstance item) {
        BodyPart slot = null;
        int location = item.getEquipSlot();

        switch (location) {
            case PAPERDOLL_PENDANT:
                slot = UNDERWEAR;
                break;
            case PAPERDOLL_LEAR:
                slot = LEFT_EAR;
                break;
            case PAPERDOLL_REAR:
                slot = RIGHT_EAR;
                break;
            case PAPERDOLL_NECK:
                slot = NECK;
                break;
            case PAPERDOLL_RFINGER:
                slot = RIGHT_FINGER;
                break;
            case PAPERDOLL_LFINGER:
                slot = LEFT_FINGER;
                break;
            case PAPERDOLL_HAIR:
                slot = HAIR;
                break;
            case PAPERDOLL_DECO1:
                slot = DECO;
                break;
            case PAPERDOLL_DHAIR:
                slot = HAIR_DOWN;
                break;
            case PAPERDOLL_HEAD:
                slot = HEAD;
                break;
            case PAPERDOLL_RHAND:
                slot = RIGHT_HAND;
                break;
            case PAPERDOLL_LHAND:
                slot = LEFT_HAND;
                break;
            case PAPERDOLL_GLOVES:
                slot = GLOVES;
                break;
            case PAPERDOLL_CHEST:
                slot = CHEST;
                break;// fall through
            case PAPERDOLL_LEGS:
                slot = LEGS;
                break;
            case PAPERDOLL_BACK:
                slot = CLOAK;
                break;
            case PAPERDOLL_FEET:
                slot = FEET;
                break;
            case PAPERDOLL_LRHAND:
                slot = TWO_HANDS;
                break;
        }

        return slot;
    }

    /**
     * Unequips item in body slot and returns alterations.
     *
     * @param slot : int designating the slot of the paperdoll
     * @return L2ItemInstance[] : list of changes
     */
    public synchronized L2ItemInstance[] unEquipItemInBodySlotAndRecord(BodyPart slot) {
        ChangeRecorder recorder = newRecorder();
        try {
            unEquipItemInBodySlot(slot);
            if (getOwner() instanceof L2PcInstance) {
                ((L2PcInstance) getOwner()).refreshExpertisePenalty();
            }
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Sets item in slot of the paperdoll to null value
     *
     * @param pdollSlot : int designating the slot
     * @return L2ItemInstance designating the item in slot before change
     */
    public synchronized L2ItemInstance unEquipItemInSlot(int pdollSlot) {
        return setPaperdollItem(pdollSlot, null);
    }

    /**
     * Unepquips item in slot and returns alterations
     *
     * @param slot : int designating the slot
     * @return L2ItemInstance[] : list of items altered
     */
    public synchronized L2ItemInstance[] unEquipItemInSlotAndRecord(int slot) {
        ChangeRecorder recorder = newRecorder();
        try {
            unEquipItemInSlot(slot);
            if (getOwner() instanceof L2PcInstance) {
                ((L2PcInstance) getOwner()).refreshExpertisePenalty();
            }
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Unequips item in slot (i.e. equips with default value)
     *
     * @param slot : int designating the slot
     */
    private void unEquipItemInBodySlot(BodyPart slot) {
        if (Config.DEBUG) {
            _log.debug("--- unequip body slot:" + slot);
        }
        int pdollSlot = -1;

        switch (slot) {
            case LEFT_EAR:
                pdollSlot = PAPERDOLL_LEAR;
                break;
            case RIGHT_EAR:
                pdollSlot = PAPERDOLL_REAR;
                break;
            case NECK:
                pdollSlot = PAPERDOLL_NECK;
                break;
            case RIGHT_FINGER:
                pdollSlot = PAPERDOLL_RFINGER;
                break;
            case LEFT_FINGER:
                pdollSlot = PAPERDOLL_LFINGER;
                break;
            case HAIR:
                pdollSlot = PAPERDOLL_HAIR;
                break;
            case DECO:
                pdollSlot = PAPERDOLL_DECO1;
                break;
            case HAIR_DOWN:
                setPaperdollItem(PAPERDOLL_HAIR, null);
                setPaperdollItem(PAPERDOLL_DHAIR, null);// this should be the same as in DHAIR
                pdollSlot = PAPERDOLL_DHAIR;
                break;
            case HEAD:
                pdollSlot = PAPERDOLL_HEAD;
                break;
            case RIGHT_HAND:
                pdollSlot = PAPERDOLL_RHAND;
                break;
            case LEFT_HAND:
                pdollSlot = PAPERDOLL_LHAND;
                break;
            case GLOVES:
                pdollSlot = PAPERDOLL_GLOVES;
                break;
            case CHEST: // fall through
            case FULL_BODY:
                pdollSlot = PAPERDOLL_CHEST;
                break;
            case LEGS:
                pdollSlot = PAPERDOLL_LEGS;
                break;
            case CLOAK:
                pdollSlot = PAPERDOLL_BACK;
                break;
            case FEET:
                pdollSlot = PAPERDOLL_FEET;
                break;
            case UNDERWEAR:
                pdollSlot = PAPERDOLL_PENDANT;
                break;
            case TWO_HANDS:
                setPaperdollItem(PAPERDOLL_LHAND, null);
                setPaperdollItem(PAPERDOLL_RHAND, null);// this should be the same as in LRHAND
                pdollSlot = PAPERDOLL_LRHAND;
                break;
        }
        if (pdollSlot >= 0) {
            setPaperdollItem(pdollSlot, null);
        }
    }

    /**
     * Equips item and returns list of alterations
     *
     * @param item : L2ItemInstance corresponding to the item
     * @return L2ItemInstance[] : list of alterations
     */
    public L2ItemInstance[] equipItemAndRecord(L2ItemInstance item) {
        ChangeRecorder recorder = newRecorder();

        try {
            equipItem(item);
        } finally {
            removePaperdollListener(recorder);
        }

        return recorder.getChangedItems();
    }

    /**
     * Equips item in slot of paperdoll.
     *
     * @param item : L2ItemInstance designating the item and slot used.
     */
    public synchronized void equipItem(L2ItemInstance item) {
        if ((getOwner() instanceof L2PcInstance) && (((L2PcInstance) getOwner()).getPrivateStoreType() != 0)) {
            return;
        }

        if (getOwner() instanceof L2PcInstance) {
            L2PcInstance player = (L2PcInstance) getOwner();

            if (!player.isGM()) {
                if (!player.isHero()) {
                    int itemId = item.getId();
                    if (((itemId >= 6611) && (itemId <= 6621)) || (itemId == 6842)) {
                        return;
                    }
                }
            }
        }

        BodyPart targetSlot = item.getBodyPart();
        switch (targetSlot) {
            case TWO_HANDS: {
                if (setPaperdollItem(PAPERDOLL_LHAND, null) != null) {
                    // exchange 2h for 2h
                    setPaperdollItem(PAPERDOLL_RHAND, null);
                    setPaperdollItem(PAPERDOLL_LHAND, null);
                } else {
                    setPaperdollItem(PAPERDOLL_RHAND, null);
                }

                setPaperdollItem(PAPERDOLL_RHAND, item);
                setPaperdollItem(PAPERDOLL_LRHAND, item);
                break;
            }
            case LEFT_HAND: {
                if ((item.getType() != ItemType.ARROW)) {
                    L2ItemInstance old1 = setPaperdollItem(PAPERDOLL_LRHAND, null);

                    if (old1 != null) {
                        setPaperdollItem(PAPERDOLL_RHAND, null);
                    }
                }

                setPaperdollItem(PAPERDOLL_LHAND, null);
                setPaperdollItem(PAPERDOLL_LHAND, item);
                break;
            }
            case RIGHT_HAND: {
                if (_paperdoll[PAPERDOLL_LRHAND] != null) {
                    setPaperdollItem(PAPERDOLL_LRHAND, null);
                    setPaperdollItem(PAPERDOLL_LHAND, null);
                    setPaperdollItem(PAPERDOLL_RHAND, null);
                } else {
                    setPaperdollItem(PAPERDOLL_RHAND, null);
                }

                setPaperdollItem(PAPERDOLL_RHAND, item);
                break;
            }
            case LEFT_EAR:
            case RIGHT_EAR:
            case EAR: {
                if (_paperdoll[PAPERDOLL_LEAR] == null) {
                    setPaperdollItem(PAPERDOLL_LEAR, item);
                } else if (_paperdoll[PAPERDOLL_REAR] == null) {
                    setPaperdollItem(PAPERDOLL_REAR, item);
                } else {
                    setPaperdollItem(PAPERDOLL_LEAR, null);
                    setPaperdollItem(PAPERDOLL_LEAR, item);
                }

                break;
            }
            case LEFT_FINGER:
            case RIGHT_FINGER:
            case FINGER: {
                if (_paperdoll[PAPERDOLL_LFINGER] == null) {
                    setPaperdollItem(PAPERDOLL_LFINGER, item);
                } else if (_paperdoll[PAPERDOLL_RFINGER] == null) {
                    setPaperdollItem(PAPERDOLL_RFINGER, item);
                } else {
                    setPaperdollItem(PAPERDOLL_LFINGER, null);
                    setPaperdollItem(PAPERDOLL_LFINGER, item);
                }

                break;
            }
            case NECK:
                setPaperdollItem(PAPERDOLL_NECK, item);
                break;
            case FULL_BODY:
                setPaperdollItem(PAPERDOLL_CHEST, null);
                setPaperdollItem(PAPERDOLL_LEGS, null);
                setPaperdollItem(PAPERDOLL_CHEST, item);
                break;
            case CHEST:
                setPaperdollItem(PAPERDOLL_CHEST, item);
                break;
            case LEGS: {
                // handle full armor
                L2ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
                if (nonNull(chest) && chest.getBodyPart() == BodyPart.FULL_BODY) {
                    setPaperdollItem(PAPERDOLL_CHEST, null);
                }

                setPaperdollItem(PAPERDOLL_LEGS, null);
                setPaperdollItem(PAPERDOLL_LEGS, item);
                break;
            }
            case FEET:
                setPaperdollItem(PAPERDOLL_FEET, item);
                break;
            case GLOVES:
                setPaperdollItem(PAPERDOLL_GLOVES, item);
                break;
            case HEAD:
                setPaperdollItem(PAPERDOLL_HEAD, item);
                break;
            case HAIR:
                if (setPaperdollItem(PAPERDOLL_DHAIR, null) != null) {
                    setPaperdollItem(PAPERDOLL_DHAIR, null);
                    setPaperdollItem(PAPERDOLL_HAIR, null);
                    setPaperdollItem(PAPERDOLL_DECO1, null);
                } else {
                    setPaperdollItem(PAPERDOLL_HAIR, null);
                }
                setPaperdollItem(PAPERDOLL_HAIR, item);
                break;
            case DECO:
                if (setPaperdollItem(PAPERDOLL_DHAIR, null) != null) {
                    setPaperdollItem(PAPERDOLL_DHAIR, null);
                    setPaperdollItem(PAPERDOLL_HAIR, null);
                    setPaperdollItem(PAPERDOLL_DECO1, null);
                } else {
                    setPaperdollItem(PAPERDOLL_DECO1, null);
                }
                setPaperdollItem(PAPERDOLL_DECO1, item);
                break;
            case HAIR_DOWN:
                if (setPaperdollItem(PAPERDOLL_HAIR, null) != null) {
                    setPaperdollItem(PAPERDOLL_HAIR, null);
                    setPaperdollItem(PAPERDOLL_DECO1, null);
                } else {
                    setPaperdollItem(PAPERDOLL_DECO1, null);
                }
                setPaperdollItem(PAPERDOLL_DHAIR, item);
                break;
            case UNDERWEAR:
                setPaperdollItem(PAPERDOLL_PENDANT, item);
                break;
            case CLOAK:
                setPaperdollItem(PAPERDOLL_BACK, item);
                break;
            default:
                _log.warn("unknown body slot:" + targetSlot);
        }
    }

    /**
     * Refresh the weight of equipment loaded
     */
    @Override
    protected void refreshWeight() {
        int weight = 0;

        for (L2ItemInstance item : _items) {
            if (nonNull(item)) {
                weight += item.getWeight() * item.getCount();
            }
        }

        _totalWeight = weight;
    }

    /**
     * Returns the totalWeight.
     *
     * @return int
     */
    public int getTotalWeight() {
        return _totalWeight;
    }

    /**
     * Return the L2ItemInstance of the arrows needed for this bow.<BR>
     * <BR>
     *
     * @param bow : ItemTemplate designating the bow
     * @return L2ItemInstance pointing out arrows for bow
     */
    public L2ItemInstance findArrowForBow(L2ItemInstance bow) {
        int arrowsId = 0;

        switch (bow.getCrystal()) {
            default: // broken weapon.csv ??
            case NONE:
                arrowsId = 17;
                break; // Wooden arrow
            case D:
                arrowsId = 1341;
                break; // Bone arrow
            case C:
                arrowsId = 1342;
                break; // Fine steel arrow
            case B:
                arrowsId = 1343;
                break; // Silver arrow
            case A:
                arrowsId = 1344;
                break; // Mithril arrow
            case S:
                arrowsId = 1345;
                break; // Shining arrow
        }

        // Get the L2ItemInstance corresponding to the item identifier and return it
        return getItemByItemId(arrowsId);
    }

    @Override
    public void restore() {
        ItemRepository repository = DatabaseAccess.getRepository(ItemRepository.class);
        repository.findAllByOwnerAndLocations(getOwner().getObjectId(), getBaseLocation().name(), getEquipLocation().name()).forEach(items -> {
            L2ItemInstance item = L2ItemInstance.restoreFromDb(items);
            if (item == null) {
                return;
            }

            if (getOwner() instanceof L2PcInstance) {
                L2PcInstance player = (L2PcInstance) getOwner();

                if (!player.isGM()) {
                    if (!player.isHero()) {
                        int itemId = item.getId();
                        if (((itemId >= 6611) && (itemId <= 6621)) || (itemId == 6842)) {
                            item.setLocation(ItemLocation.INVENTORY);
                        }
                    }
                }
            }

            L2World.getInstance().storeObject(item);

            // If stackable item is found in inventory just add to current quantity
            if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                addItem("Restore", item, null, getOwner());
            } else {
                addItem(item);
            }
        });
    }

    /**
     * Re-notify to paperdoll listeners every equipped item
     */
    public void reloadEquippedItems() {

        L2ItemInstance item;
        int slot;

        for (L2ItemInstance element : _paperdoll) {
            item = element;
            if (item == null) {
                continue;
            }
            slot = item.getEquipSlot();

            for (PaperdollListener listener : _paperdollListeners) {
                if (listener == null) {
                    continue;
                }
                listener.notifyUnequiped(slot, item);
                listener.notifyEquiped(slot, item);
            }
        }
    }
}