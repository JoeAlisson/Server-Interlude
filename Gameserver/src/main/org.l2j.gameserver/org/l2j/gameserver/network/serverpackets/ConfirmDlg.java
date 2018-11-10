package org.l2j.gameserver.network.serverpackets;

/**
 * @author Dezmond_snz Format: cdddsdd
 */
public class ConfirmDlg extends L2GameServerPacket {
	private final int _requestId;
	private final String _name;
	
	public ConfirmDlg(int requestId, String requestorName)
	{
		_requestId = requestId;
		_name = requestorName;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xed);
		writeInt(_requestId);
		writeInt(0x02); // ??
		writeInt(0x00); // ??
		writeString(_name);
		writeInt(0x01); // ??
		writeInt(0x00); // ??
	}

}
