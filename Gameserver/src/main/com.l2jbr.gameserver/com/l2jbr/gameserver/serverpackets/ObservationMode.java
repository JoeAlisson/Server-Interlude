/*
 * This program is free software; you can redistribute it and/or modify
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
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ObservationMode extends L2GameServerPacket
{
	// ddSS
	private static final String _S__DF_OBSERVMODE = "[S] DF ObservationMode";
	private final int _x, _y, _z;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public ObservationMode(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xdf);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeByte(0x00);
		writeByte(0xc0);
		writeByte(0x00);
	}
	
	@Override
	public String getType()
	{
		return _S__DF_OBSERVMODE;
	}
}
