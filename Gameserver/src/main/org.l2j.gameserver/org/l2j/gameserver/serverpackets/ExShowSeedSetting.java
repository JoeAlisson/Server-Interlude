package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2Manor;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.database.SeedProduction;

import java.util.List;

/**
 * format(packet 0xFE) ch dd [ddcdcdddddddd] c - id h - sub id d - manor id d - size [ d - seed id d - level c d - reward 1 id c d - reward 2 id d - next sale limit d - price for castle to produce 1 d - min seed price d - max seed price d - today sales d - today price d - next sales d - next price ]
 *
 * @author l3x
 */
public class ExShowSeedSetting extends L2GameServerPacket {

    private final int _manorId;
    private final int _count;
    private final long[] _seedData; // data to send, size:_count*12

    @Override
    public void runImpl() {
    }

    public ExShowSeedSetting(int manorId) {
        _manorId = manorId;
        Castle c = CastleManager.getInstance().getCastleById(_manorId);
        List<Integer> seeds = L2Manor.getInstance().getSeedsForCastle(_manorId);
        _count = seeds.size();
        _seedData = new long[_count * 12];
        int i = 0;
        for (int s : seeds) {
            _seedData[(i * 12) + 0] = s;
            _seedData[(i * 12) + 1] = L2Manor.getInstance().getSeedLevel(s);
            _seedData[(i * 12) + 2] = L2Manor.getInstance().getRewardItemBySeed(s, 1);
            _seedData[(i * 12) + 3] = L2Manor.getInstance().getRewardItemBySeed(s, 2);
            _seedData[(i * 12) + 4] = L2Manor.getInstance().getSeedSaleLimit(s);
            _seedData[(i * 12) + 5] = L2Manor.getInstance().getSeedBuyPrice(s);
            _seedData[(i * 12) + 6] = (L2Manor.getInstance().getSeedBasicPrice(s) * 60) / 100;
            _seedData[(i * 12) + 7] = L2Manor.getInstance().getSeedBasicPrice(s) * 10;
            SeedProduction seedPr = c.getSeed(s, CastleManorManager.PERIOD_CURRENT);
            if (seedPr != null) {
                _seedData[(i * 12) + 8] = seedPr.getStartAmount();
                _seedData[(i * 12) + 9] = seedPr.getPrice();
            } else {
                _seedData[(i * 12) + 8] = 0;
                _seedData[(i * 12) + 9] = 0;
            }
            seedPr = c.getSeed(s, CastleManorManager.PERIOD_NEXT);
            if (seedPr != null) {
                _seedData[(i * 12) + 10] = seedPr.getStartAmount();
                _seedData[(i * 12) + 11] = seedPr.getPrice();
            } else {
                _seedData[(i * 12) + 10] = 0;
                _seedData[(i * 12) + 11] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeImpl() {
        writeByte(0xFE); // Id
        writeShort(0x1F); // SubId

        writeInt(_manorId); // manor id
        writeInt(_count); // size

        for (int i = 0; i < _count; i++) {
            writeInt((int) _seedData[(i * 12) + 0]); // seed id
            writeInt((int) _seedData[(i * 12) + 1]); // level
            writeByte(1);
            writeInt((int) _seedData[(i * 12) + 2]); // reward 1 id
            writeByte(1);
            writeInt((int) _seedData[(i * 12) + 3]); // reward 2 id

            writeInt((int) _seedData[(i * 12) + 4]); // next sale limit
            writeInt((int) _seedData[(i * 12) + 5]); // price for castle to produce 1
            writeInt((int) _seedData[(i * 12) + 6]); // min seed price
            writeInt((int) _seedData[(i * 12) + 7]); // max seed price

            writeInt((int) _seedData[(i * 12) + 8]); // today sales
            writeInt((int) _seedData[(i * 12) + 9]); // today price
            writeInt((int) _seedData[(i * 12) + 10]); // next sales
            writeInt((int) _seedData[(i * 12) + 11]); // next price
        }
    }
}
