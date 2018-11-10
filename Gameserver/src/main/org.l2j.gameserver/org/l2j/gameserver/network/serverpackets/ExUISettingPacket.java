package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

public class ExUISettingPacket extends L2GameServerPacket {

    private final byte data[];

    // TODO Implements KeyBindings
	public ExUISettingPacket(L2PcInstance player)
	{
		data = new byte[0];
	}

	@Override
	protected void writeImpl() {
		writeByte(0xFE);
		writeShort(0x71);
		writeInt(data.length);
		writeBytes(data);
	}

    @Override
    protected int packetSize() {
        return data.length + 9;
    }
}
