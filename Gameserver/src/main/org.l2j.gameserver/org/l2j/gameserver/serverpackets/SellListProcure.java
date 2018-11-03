package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.database.CropProcure;

import java.util.*;

public class SellListProcure extends L2GameServerPacket {

    private final L2PcInstance _activeChar;
    private final long _money;
    private final Map<L2ItemInstance, Integer> _sellList = new HashMap<>();
    private List<CropProcure> _procureList;
    private final int _castle;

    public SellListProcure(L2PcInstance player, int castleId) {
        _money = player.getAdena();
        _activeChar = player;
        _castle = castleId;
        _procureList = CastleManager.getInstance().getCastleById(_castle).getCropProcure(0);
        for (CropProcure c : _procureList) {
            L2ItemInstance item = _activeChar.getInventory().getItemByItemId(c.getCropId());
            if ((item != null) && (c.getAmount() > 0)) {
                _sellList.put(item, c.getAmount());
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0xE9);
        writeLong(_money); // money
        writeInt(0x00); // lease ?
        writeShort(_sellList.size()); // list size

        for (L2ItemInstance item : _sellList.keySet()) {
            writeShort(item.getItem().getType().ordinal());
            writeInt(item.getObjectId());
            writeInt(item.getItemId());
            writeInt(_sellList.get(item)); // count
            writeShort(item.getItem().getCommissionType().ordinal());
            writeShort(0); // unknown
            writeInt(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }
}
