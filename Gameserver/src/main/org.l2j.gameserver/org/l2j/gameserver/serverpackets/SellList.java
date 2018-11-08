package org.l2j.gameserver.serverpackets;

import org.l2j.commons.Config;
import org.l2j.gameserver.model.L2ItemInstance;
import org.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SellList extends L2GameServerPacket {
    private static Logger _log = LoggerFactory.getLogger(SellList.class.getName());
    private final L2PcInstance _activeChar;
    private final L2MerchantInstance _lease;
    private final long _money;
    private final List<L2ItemInstance> _selllist = new LinkedList<>();

    public SellList(L2PcInstance player) {
        _activeChar = player;
        _lease = null;
        _money = _activeChar.getAdena();
        doLease();
    }

    public SellList(L2PcInstance player, L2MerchantInstance lease) {
        _activeChar = player;
        _lease = lease;
        _money = _activeChar.getAdena();
        doLease();
    }

    private void doLease() {
        if (_lease == null) {
            for (L2ItemInstance item : _activeChar.getInventory().getItems()) {
                if (!item.isEquipped() && // Not equipped
                        item.isSellable() && // Item is sellable
                        ((_activeChar.getPet() == null) || // Pet not summoned or
                             (item.getObjectId() != _activeChar.getPet().getControlItemId()))) // Pet is summoned and not the item that summoned the pet
                {
                    _selllist.add(item);
                    if (Config.DEBUG) {
                        _log.debug("item added to selllist: " + item.getName());
                    }
                }
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x10);
        writeLong(_money);
        writeInt(_lease == null ? 0x00 : 1000000 + _lease.getNpcId());

        writeShort(_selllist.size());

        for (L2ItemInstance item : _selllist) {
            writeShort(item.getType().ordinal());
            writeInt(item.getObjectId());
            writeInt(item.getId());
            writeLong(item.getCount());
            writeShort(item.getCommissionType().ordinal());
            writeShort(0x00);
            writeInt(0); //item.getItem().getBodyPart().getId());
            writeShort(item.getEnchantLevel());
            writeShort(0x00);
            writeShort(0x00);

            if (_lease == null) {
                writeLong(item.getPrice() / 2); // wtf??? there is no conditional part in SellList!! this d should allways be here 0.o! fortunately the lease stuff are never ever use so the if allways exectues
            }
        }
    }
}
