package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.templates.base.PaperDoll;

import static java.util.Objects.nonNull;

public class ExUserInfoEquipSlot extends AbstractMaskPacket<PaperDoll> {

    private static final int PAPER_DOLL_INFO_SIZE = 22;
    private final byte[] _masks = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    @Override
    protected byte[] getMasks() {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(PaperDoll component) {
    }

    public ExUserInfoEquipSlot() {
        addComponentType(PaperDoll.values());
    }

    public ExUserInfoEquipSlot(PaperDoll paperDoll) {
        addComponentType(paperDoll);
    }

    @Override
    protected final void writeImpl() {
        var player = client.getActiveChar();
        writeByte(0xFE);
        writeShort(0x156);
        writeInt(player.getObjectId());
        writeShort(PaperDoll.values().length);
        writeBytes(_masks);

        for (PaperDoll slot : PaperDoll.values()) {
            if (containsMask(slot)) {
                var item = player.getItemOnPaperDoll(slot);
                writeShort(PAPER_DOLL_INFO_SIZE);
                if(nonNull(item)) {
                    writeInt(item.getObjectId());
                    writeInt(item.getId());
                } else {
                    writeInt(0);
                    writeInt(0);
                }

                writeInt(0); // TODO implement Argument Variation
                writeInt(0); // TODO implement Argument Variation
                writeInt(0); // TODO implement Visual Item
            }
        }
    }
}