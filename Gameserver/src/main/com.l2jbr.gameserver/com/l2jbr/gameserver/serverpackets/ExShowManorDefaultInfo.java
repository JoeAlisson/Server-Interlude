package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.L2Manor;

import java.util.List;

/**
 * format(packet 0xFE) ch cd [ddddcdcd] c - id h - sub id c d - size [ d - level d - seed price d - seed level d - crop price c d - reward 1 id c d - reward 2 id ]
 *
 * @author l3x
 */
public class ExShowManorDefaultInfo extends L2GameServerPacket {
    private static final String _S__FE_1C_EXSHOWSEEDINFO = "[S] FE:1E ExShowManorDefaultInfo";

    private List<Integer> _crops = null;

    public ExShowManorDefaultInfo() {
        _crops = L2Manor.getInstance().getAllCrops();
    }

    @Override
    protected void writeImpl() {
        writeByte(0xFE);
        writeShort(0x1E);
        writeByte(0);
        writeInt(_crops.size());
        for (int cropId : _crops) {
            writeInt(cropId); // crop Id
            writeInt(L2Manor.getInstance().getSeedLevelByCrop(cropId)); // level
            writeInt(L2Manor.getInstance().getSeedBasicPriceByCrop(cropId)); // seed price
            writeInt(L2Manor.getInstance().getCropBasicPrice(cropId)); // crop price
            writeByte(1); // rewrad 1 Type
            writeInt(L2Manor.getInstance().getRewardItem(cropId, 1)); // Rewrad 1 Type Item Id
            writeByte(1); // rewrad 2 Type
            writeInt(L2Manor.getInstance().getRewardItem(cropId, 2)); // Rewrad 2 Type Item Id
        }
    }
}
