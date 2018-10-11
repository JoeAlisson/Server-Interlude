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
package org.l2j.authserver.network.packet.client2auth;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.packet.L2LoginClientPacket;
import org.l2j.authserver.network.packet.auth2client.LoginFail.LoginFailReason;
import org.l2j.authserver.network.packet.auth2client.PlayFail.PlayFailReason;
import org.l2j.authserver.network.packet.auth2client.PlayOk;


/**
 * Fromat is ddc d: first part of session id d: second part of session id c: server ID
 */
public class RequestServerLogin extends L2LoginClientPacket
{
	private int _skey1;
	private int _skey2;
	private int _serverId;
	
	/**
	 * @return
	 */
	public int getSessionKey1()
	{
		return _skey1;
	}
	
	/**
	 * @return
	 */
	public int getSessionKey2()
	{
		return _skey2;
	}
	
	/**
	 * @return
	 */
	public int getServerID()
	{
		return _serverId;
	}
	
	@Override
	public boolean readImpl()
	{
		if (availableData() >= 9)
		{
			_skey1 = readInt();
			_skey2 = readInt();
			_serverId = readUnsignedByte();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		SessionKey sk = getClient().getSessionKey();
		
		// If we didn't showed the license we can't check these values
		if (sk.checkLoginPair(_skey1, _skey2))
		{
			if (AuthController.getInstance().isLoginPossible(getClient(), _serverId))
			{
				getClient().setJoinedGS(true);
				getClient().sendPacket(new PlayOk(sk));
			}
			else
			{
				getClient().close(PlayFailReason.REASON_TOO_MANY_PLAYERS);
			}
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
