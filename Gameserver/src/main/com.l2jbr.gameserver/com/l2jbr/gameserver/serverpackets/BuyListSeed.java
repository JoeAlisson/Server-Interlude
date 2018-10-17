package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.entity.database.MerchantItem;
import com.l2jbr.gameserver.model.entity.database.MerchantShop;

public final class BuyListSeed extends L2GameServerPacket {

    private final int _manorId;
    private final int _money;
    private final MerchantShop shop;

    public BuyListSeed(MerchantShop shop, int manorId, int currentMoney) {
        this.shop = shop;
        _money = currentMoney;
        _manorId = manorId;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xE8);

        writeInt(_money); // current money
        writeInt(_manorId); // manor id

        writeShort(shop.getItems().size()); // list length

        for (MerchantItem item : shop.getItems()) {
            writeShort(0x04); // item->type1
            writeInt(0x00); // objectId
            writeInt(item.getItemId()); // item id
            writeInt(item.getCount()); // item count
            writeShort(0x04); // item->type2
            writeShort(0x00); // unknown :)
            writeInt(item.getPrice()); // price
        }
    }
}