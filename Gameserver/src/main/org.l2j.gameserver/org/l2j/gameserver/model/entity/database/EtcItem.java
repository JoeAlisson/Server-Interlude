package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.templates.BodyPart;
import org.l2j.gameserver.templates.ItemType;
import org.l2j.gameserver.templates.ItemTypeGroup;
import org.springframework.data.annotation.Transient;

@Table("etcitem")
public class EtcItem extends ItemTemplate {

    @Column("item_type")
    private ItemType itemType;
    @Column("consume_type")
    private ConsumeType consumeType;

    @Transient
    private BodyPart bodyPart = BodyPart.NONE;
    @Transient
    private boolean stackable = false;

    public EtcItem() {
        type1 = ItemTypeGroup.TYPE1_ITEM_QUEST;
        type2 = ItemTypeGroup.TYPE2_OTHER;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        switch (consumeType) {
            case ASSET:
                itemType = ItemType.MONEY;
                type2 = ItemTypeGroup.TYPE2_MONEY;
            case STACKABLE:
                stackable = true;
        }

        switch (itemType) {
            case ARROW:
            case LURE:
                bodyPart = BodyPart.LEFT_HAND;
                break;
            case QUEST:
                type2 = ItemTypeGroup.TYPE2_QUEST;
        }
    }

    @Override
    public ItemType getType() { return itemType; }

    @Override
    public BodyPart getBodyPart() {
        return bodyPart;
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public boolean isEquipable() {
        return false;
    }

    enum ConsumeType {
        NORMAL,
        STACKABLE,
        ASSET;
    }
}
