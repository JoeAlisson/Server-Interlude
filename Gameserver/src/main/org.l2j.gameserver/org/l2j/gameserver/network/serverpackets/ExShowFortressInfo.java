package org.l2j.gameserver.network.serverpackets;

public class ExShowFortressInfo extends L2GameServerPacket {

    @Override
    protected void writeImpl() {
        writeByte(0xFE);
        writeShort(0x15);

        writeInt(0); // Fort list size

        // TODO implement Fortress
        /*Collection<Fort> forts = FortManager.getInstance().getForts();
        Iterator var3 = forts.iterator();

        while(var3.hasNext()) {
            Fort fort = (Fort)var3.next();
            L2Clan clan = fort.getOwnerClan();
            writeInt(fort.getResidenceId()); // fort id
            writeString(clan != null ? clan.getName() : "");
            writeInt(fort.getSiege().isInProgress() ? 1 : 0);
            writeInt(fort.getOwnedTime());
        }*/
    }
}
