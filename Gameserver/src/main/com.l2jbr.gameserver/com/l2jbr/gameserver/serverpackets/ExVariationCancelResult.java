/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jbr.gameserver.serverpackets;


/**
 * Format: (ch)ddd
 */
public class ExVariationCancelResult extends L2GameServerPacket
{
	private static final String _S__FE_57_EXVARIATIONCANCELRESULT = "[S] FE:57 ExVariationCancelResult";
	
	private final int _closeWindow;
	private final int _unk1;
	
	public ExVariationCancelResult(int result)
	{
		_closeWindow = 1;
		_unk1 = result;
	}
	
	@Override
	protected void writeImpl()
	{
		writeByte(0xfe);
		writeShort(0x57);
		writeInt(_closeWindow);
		writeInt(_unk1);
	}
	
	@Override
	public String getType()
	{
		return _S__FE_57_EXVARIATIONCANCELRESULT;
	}
}