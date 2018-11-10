package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.LinkedList;
import java.util.List;

/**
 * sample 63 01 00 00 00 count c1 b2 e0 4a object id 54 00 75 00 65 00 73 00 64 00 61 00 79 00 00 00 name 5a 01 00 00 hp 5a 01 00 00 hp max 89 00 00 00 mp 89 00 00 00 mp max 0e 00 00 00 level 12 00 00 00 class 00 00 00 00 01 00 00 00 format d (dSdddddddd)
 *
 * @version $Revision: 1.6.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $
 */
public class PartySmallWindowAll extends L2GameServerPacket {
    private List<L2PcInstance> _partyMembers = new LinkedList<>();

    public void setPartyList(List<L2PcInstance> party) {
        _partyMembers = party;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x4e);
        L2PcInstance player = getClient().getActiveChar();
        writeInt(_partyMembers.get(0).getObjectId()); // c3 party leader id
        writeInt(_partyMembers.get(0).getParty().getLootDistribution());// c3 party loot type (0,1,2,....)
        writeInt(_partyMembers.size() - 1);

        for (int i = 0; i < _partyMembers.size(); i++) {
            L2PcInstance member = _partyMembers.get(i);
            if (!member.equals(player)) {
                writeInt(member.getObjectId());
                writeString(member.getName());

                writeInt((int) member.getCurrentCp()); // c4
                writeInt(member.getMaxCp()); // c4

                writeInt((int) member.getCurrentHp());
                writeInt(member.getMaxHp());
                writeInt((int) member.getCurrentMp());
                writeInt(member.getMaxMp());
                writeInt(member.getLevel());
                writeInt(member.getPlayerClass().getId());
                writeInt(0);// writeInt(0x01); ??
                writeInt(0);
            }
        }
    }
}
