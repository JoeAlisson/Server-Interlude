package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.l2j.gameserver.model.ItemLocation;
import org.springframework.data.annotation.Id;

@Table("items")
public class ItemEntity extends Entity<Integer> {

    @Id
    @Column("object_id")
    private int objectId;
    @Column("owner_id")
    private int ownerId;
    @Column("item_id")
    private int itemId;
    private long count;
    @Column("enchant_level")
    private int enchantLevel;
    @Column("loc")
    private ItemLocation location;
    @Column("loc_data")
    private int locData;
    @Column("price_sell")
    private int priceSell;
    @Column("price_buy")
    private int priceBuy;
    @Column("time_of_use")
    private Integer timeOfUse;
    @Column("custom_type1")
    private int customType1;
    @Column("custom_type2")
    private int subType;
    @Column("mana_left")
    private long manaLeft;

    public ItemEntity() { }

    public ItemEntity(int objectId, int ownerId, int itemId, long count, String loc, int locData, int enchantLevel, int priceSell, int priceBuy, int type1, int type2, long mana) {
        this.objectId = objectId;
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.count = count;
        this.location = ItemLocation.valueOf(loc);
        this.locData = locData;
        this.enchantLevel = enchantLevel;
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
        this.customType1 = type1;
        this.subType = type2;
        this.manaLeft = (int) mana;
    }

    @Override
    public Integer getId() {  return objectId; }

    public int getObjectId() {
        return objectId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public ItemLocation getLocation() {
        return location;
    }

    public int getLocData() {
        return locData;
    }

    public int getPriceSell() {
        return priceSell;
    }

    public int getPriceBuy() {
        return priceBuy;
    }

    public int getTimeOfUse() {
        return timeOfUse;
    }

    public int getCustomType1() {
        return customType1;
    }

    public int getSubType() {
        return subType;
    }

    public long getManaLeft() {
        return manaLeft;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setLocation(ItemLocation location) {
        this.location = location;
    }

    public void setMana(long time) {
        this.manaLeft = time;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public void setLocData(int locData) {
        this.locData = locData;
    }

    public void changeCount(long count) {
        if(count > 0 && Long.MAX_VALUE - count < this.count) {
            this.count = Long.MAX_VALUE;
        } else {
            this.count += count;
        }

        if(this.count < 0) {
            this.count = 0;
        }
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
