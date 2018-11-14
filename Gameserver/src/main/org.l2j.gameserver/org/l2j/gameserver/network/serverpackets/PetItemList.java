package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;

public class PetItemList extends L2GameServerPacket {

    private final L2PetInstance _activeChar;

    public PetItemList(L2PetInstance character) {
        _activeChar = character;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xB2);

        var items = _activeChar.getInventory().getItems();
        int count = items.size();
        writeShort(count);

        for (L2ItemInstance temp : items) {
            writeShort(temp.getType().ordinal()); // item type1
            writeInt(temp.getObjectId());
            writeInt(temp.getId());
            writeLong(temp.getCount());
            writeShort(temp.getCommissionType().ordinal()); // item type2
            writeShort(0xff); // ?
            if (temp.isEquipped()) {
                writeShort(0x01);
            } else {
                writeShort(0x00);
            }
            writeInt(0); // TODO temp.getItem().getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
            // writeShort(temp.getItem().getBodyPart()); // rev 377 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
            writeShort(temp.getEnchantLevel()); // enchant level
            writeShort(0x00); // ?
        }
    }
}