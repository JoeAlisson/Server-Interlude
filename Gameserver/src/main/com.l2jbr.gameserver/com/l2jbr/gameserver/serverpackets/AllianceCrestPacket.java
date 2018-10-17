package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.commons.Config;

public class AllianceCrestPacket extends L2GameServerPacket {

	private final int _crestId;
	private final int _crestSize;
	private byte[] _data;
	
	public AllianceCrestPacket(int crestId, byte[] data) {
		_crestId = crestId;
		_data = data;
		_crestSize = _data.length;
	}
	
	@Override
	protected final void writeImpl() {
		writeByte(0xAF);
		writeInt(Config.SERVER_ID);
		writeInt(_crestId);
		writeInt(_crestSize);
		writeBytes(_data);
	}

    @Override
    protected int packetSize() {
        return _crestSize + 15;
    }
}
