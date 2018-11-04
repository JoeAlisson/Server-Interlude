package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import static java.util.Objects.requireNonNullElse;

public class CharacterSelectedPacket extends L2GameServerPacket {

    private final L2PcInstance player;

    public CharacterSelectedPacket(L2PcInstance player) {
        this.player = player;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x0B);
        writeString(player.getName());
        writeInt(player.getObjectId());
        writeString(player.getTitle());
        writeInt(client.getSessionId().sessionId);
        writeInt(player.getClanId());
        writeInt(0);  // builder level GM??
        writeInt(player.getSex());
        writeInt(player.getRace().ordinal());
        writeInt(player.getClassId());
        writeInt(1);
        writeInt(player.getX());
        writeInt(player.getY());
        writeInt(player.getZ());
        writeDouble(player.getCurrentHp());
        writeDouble(player.getCurrentMp());
        writeLong(player.getSkillPoints());
        writeLong(player.getExperience());
        writeInt(player.getLevel());
        writeInt(player.getKarma()); // Reputation
        writeInt(player.getPkKills());
        writeInt(GameTimeController.getInstance().getGameTime());
        writeInt(0);
        writeInt(player.getBaseClass());
        writeInt(0); // GG
        writeInt(0); // GG
        writeInt(0); // GG
        writeInt(0); // GG
        writeBytes(new byte[64]); // GG
        writeInt(0); // Opcode Shuffling seed
    }

    @Override
    protected int packetSize() {
        return 2 * player.getName().length() + 2 * requireNonNullElse(player.getTitle(), "").length() + 190;
    }
}
