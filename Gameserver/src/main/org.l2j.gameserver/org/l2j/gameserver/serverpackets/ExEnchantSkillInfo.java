package org.l2j.gameserver.serverpackets;

import java.util.LinkedList;
import java.util.List;


public class ExEnchantSkillInfo extends L2GameServerPacket {
    private final List<Req> _reqs;
    private final int _id;
    private final int _level;
    private final int _spCost;
    private final int _xpCost;
    private final int _rate;

    class Req {
        public int id;
        public int count;
        public int type;
        public int unk;

        Req(int pType, int pId, int pCount, int pUnk) {
            id = pId;
            type = pType;
            count = pCount;
            unk = pUnk;
        }
    }

    public ExEnchantSkillInfo(int id, int level, int spCost, int xpCost, int rate) {
        _reqs = new LinkedList<>();
        _id = id;
        _level = level;
        _spCost = spCost;
        _xpCost = xpCost;
        _rate = rate;
    }

    public void addRequirement(int type, int id, int count, int unk) {
        _reqs.add(new Req(type, id, count, unk));
    }

    /*
     * (non-Javadoc)
     * @see org.l2j.gameserver.serverpackets.ServerBasePacket#writeImpl()
     */
    @Override
    protected void writeImpl() {
        writeByte(0xfe);
        writeShort(0x18);

        writeInt(_id);
        writeInt(_level);
        writeInt(_spCost);
        writeLong(_xpCost);
        writeInt(_rate);

        writeInt(_reqs.size());

        for (Req temp : _reqs) {
            writeInt(temp.type);
            writeInt(temp.id);
            writeInt(temp.count);
            writeInt(temp.unk);
        }

    }
}