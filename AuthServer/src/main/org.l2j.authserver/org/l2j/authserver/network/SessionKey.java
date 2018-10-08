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
package org.l2j.authserver.network;

import com.l2jbr.commons.Config;
import org.l2j.authserver.network.serverpackets.LoginOk;
import org.l2j.authserver.network.serverpackets.PlayOk;


/**
 * <p>
 * This class is used to represent session keys used by the client to authenticate in the gameserver
 * </p>
 * <p>
 * A SessionKey is made up of two 8 bytes keys. One is send in the {@link LoginOk} packet and the other is sent in {@link PlayOk}
 * </p>
 * @author -Wooden-
 */
public class SessionKey
{
	public int playOkID1;
	public int playOkID2;
	public int accountId;
	public int authKey;
	
	public SessionKey(int accountId, int authKey, int playOK1, int playOK2)
	{
		playOkID1 = playOK1;
		playOkID2 = playOK2;
		this.accountId = accountId;
		this.authKey = authKey;
	}
	
	@Override
	public String toString()
	{
		return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + accountId + " " + authKey;
	}
	
	public boolean checkLoginPair(int accountId, int authKey) {
		return this.accountId == accountId && this.authKey == authKey;
	}
	
	/**
	 * <p>
	 * Returns true if keys are equal.
	 * </p>
	 * <p>
	 * Only checks the PlayOk part of the session key if server doesnt show the licence when player logs in.
	 * </p>
	 * @param key
	 * @return
	 */
	public boolean equals(SessionKey key)
	{
		// when server doesnt show licence it deosnt send the LoginOk packet, client doesnt have this part of the key then.
		if (Config.SHOW_LICENCE)
		{
			return ((playOkID1 == key.playOkID1) && (accountId == key.accountId) && (playOkID2 == key.playOkID2) && (authKey == key.authKey));
		}
		return ((playOkID1 == key.playOkID1) && (playOkID2 == key.playOkID2));
	}
}