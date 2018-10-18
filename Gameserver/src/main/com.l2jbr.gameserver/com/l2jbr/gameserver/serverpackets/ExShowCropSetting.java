package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.instancemanager.CastleManager;
import com.l2jbr.gameserver.instancemanager.CastleManorManager;
import com.l2jbr.gameserver.model.L2Manor;
import com.l2jbr.gameserver.model.entity.Castle;
import com.l2jbr.gameserver.model.entity.database.CropProcure;

import java.util.List;

/**
 * format(packet 0xFE) ch dd [ddcdcdddddddcddc] c - id h - sub id d - manor id d - size [ d - crop id d - seed level c d - reward 1 id c d - reward 2 id d - next sale limit d d - min crop price d - max crop price d - today buy d - today price c - today reward d - next buy d - next price c - next
 * reward ]
 *
 * @author l3x
 */
public class ExShowCropSetting extends L2GameServerPacket {
    private static final String _S__FE_20_EXSHOWCROPSETTING = "[S] FE:20 ExShowCropSetting";

    private final int _manorId;
    private final int _count;
    private final int[] _cropData; // data to send, size:_count*14

    @Override
    public void runImpl() {
    }

    public ExShowCropSetting(int manorId) {
        _manorId = manorId;
        Castle c = CastleManager.getInstance().getCastleById(_manorId);
        List<Integer> crops = L2Manor.getInstance().getCropsForCastle(_manorId);
        _count = crops.size();
        _cropData = new int[_count * 14];
        int i = 0;
        for (int cr : crops) {
            _cropData[(i * 14) + 0] = cr;
            _cropData[(i * 14) + 1] = L2Manor.getInstance().getSeedLevelByCrop(cr);
            _cropData[(i * 14) + 2] = L2Manor.getInstance().getRewardItem(cr, 1);
            _cropData[(i * 14) + 3] = L2Manor.getInstance().getRewardItem(cr, 2);
            _cropData[(i * 14) + 4] = L2Manor.getInstance().getCropPuchaseLimit(cr);
            _cropData[(i * 14) + 5] = 0; // Looks like not used
            _cropData[(i * 14) + 6] = (L2Manor.getInstance().getCropBasicPrice(cr) * 60) / 100;
            _cropData[(i * 14) + 7] = L2Manor.getInstance().getCropBasicPrice(cr) * 10;
            CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
            if (cropPr != null) {
                _cropData[(i * 14) + 8] = cropPr.getStartAmount();
                _cropData[(i * 14) + 9] = cropPr.getPrice();
                _cropData[(i * 14) + 10] = cropPr.getReward();
            } else {
                _cropData[(i * 14) + 8] = 0;
                _cropData[(i * 14) + 9] = 0;
                _cropData[(i * 14) + 10] = 0;
            }
            cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
            if (cropPr != null) {
                _cropData[(i * 14) + 11] = cropPr.getStartAmount();
                _cropData[(i * 14) + 12] = cropPr.getPrice();
                _cropData[(i * 14) + 13] = cropPr.getReward();
            } else {
                _cropData[(i * 14) + 11] = 0;
                _cropData[(i * 14) + 12] = 0;
                _cropData[(i * 14) + 13] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeImpl() {
        writeByte(0xFE); // Id
        writeShort(0x20); // SubId

        writeInt(_manorId); // manor id
        writeInt(_count); // size

        for (int i = 0; i < _count; i++) {
            writeInt(_cropData[(i * 14) + 0]); // crop id
            writeInt(_cropData[(i * 14) + 1]); // seed level
            writeByte(1);
            writeInt(_cropData[(i * 14) + 2]); // reward 1 id
            writeByte(1);
            writeInt(_cropData[(i * 14) + 3]); // reward 2 id

            writeInt(_cropData[(i * 14) + 4]); // next sale limit
            writeInt(_cropData[(i * 14) + 5]); // ???
            writeInt(_cropData[(i * 14) + 6]); // min crop price
            writeInt(_cropData[(i * 14) + 7]); // max crop price

            writeInt(_cropData[(i * 14) + 8]); // today buy
            writeInt(_cropData[(i * 14) + 9]); // today price
            writeByte(_cropData[(i * 14) + 10]); // today reward

            writeInt(_cropData[(i * 14) + 11]); // next buy
            writeInt(_cropData[(i * 14) + 12]); // next price
            writeByte(_cropData[(i * 14) + 13]); // next reward
        }
    }
}
