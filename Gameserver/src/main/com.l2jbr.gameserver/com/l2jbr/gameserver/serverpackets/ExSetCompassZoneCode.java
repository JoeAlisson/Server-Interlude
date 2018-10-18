package com.l2jbr.gameserver.serverpackets;

/**
 * Format: ch d
 * @author KenM
 */
public class ExSetCompassZoneCode extends L2GameServerPacket
{

	public static final int SIEGEWARZONE1 = 0x0A;
	public static final int SIEGEWARZONE2 = 0x0B;
	public static final int PEACEZONE = 0x0C;
	public static final int SEVENSIGNSZONE = 0x0D;
	public static final int PVPZONE = 0x0E;
	public static final int GENERALZONE = 0x0F;
	
	private final int _zoneType;
	
	public ExSetCompassZoneCode(int val)
	{
		_zoneType = val;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xFE);
		writeShort(0x32);
		writeInt(_zoneType);
	}
}
