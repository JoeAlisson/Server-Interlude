package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("items")
public class Items extends Entity<Integer> {

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
    private String loc;
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
    private int customType2;
    @Column("mana_left")
    private int manaLeft;

    public Items() { }

    public Items(int objectId, int ownerId, int itemId, long count, String loc, int locData, int enchantLevel, int priceSell, int priceBuy, int type1, int type2, long mana) {
        this.objectId = objectId;
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.count = count;
        this.loc = loc;
        this.locData = locData;
        this.enchantLevel = enchantLevel;
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
        this.customType1 = type1;
        this.customType2 = type2;
        this.manaLeft = (int) mana;
    }

    @Override
    public Integer getId() {  return objectId; }

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

    public String getLoc() {
        return loc;
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

    public int getCustomType2() {
        return customType2;
    }

    public int getManaLeft() {
        return manaLeft;
    }
}
