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
package org.l2j.authserver.network.serverpackets;

import org.l2j.authserver.network.SessionKey;

/**
 * Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ? b: 16 bytes - unknown
 */
public final class LoginOk extends L2LoginServerPacket
{
	private final int accountId, authKey;
	
	public LoginOk(SessionKey sessionKey)
	{
		accountId = sessionKey.accountId;
		authKey = sessionKey.authKey;
	}
	
	@Override
	protected void write()
	{
		writeByte(0x03);
		writeInt(accountId);
		writeInt(authKey);
		writeBytes(new byte[8]);
		writeInt(0x000003ea); // billing type: 1002 Free, x200 paid time, x500 flat rate pre paid, others subscription
		writeInt(0x00); // paid time
		writeInt(0x00);
		writeInt(0x00); // warning mask
		writeBytes(new byte[16]); // forbidden servers
		writeInt(0x00);
	}

	@Override
	protected int packetSize() {
		return super.packetSize() + 53;
	}
}
