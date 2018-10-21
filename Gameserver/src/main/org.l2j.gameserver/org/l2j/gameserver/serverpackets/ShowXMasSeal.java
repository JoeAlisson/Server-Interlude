package org.l2j.gameserver.serverpackets;

/**
 * @author devScarlet & mrTJO
 */
public class ShowXMasSeal extends L2GameServerPacket
{
	private final int _item;
	
	public ShowXMasSeal(int item)
	{
		_item = item;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xF2);
		writeInt(_item);
	}
}
