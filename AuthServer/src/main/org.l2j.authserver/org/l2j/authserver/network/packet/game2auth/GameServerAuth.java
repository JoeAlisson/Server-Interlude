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
package org.l2j.authserver.network.packet.game2auth;

import org.l2j.authserver.network.packet.ClientBasePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Format: cccddb c desired ID c accept alternative ID c reserve Host s ExternalHostName s InetranlHostName d max players d hexid size b hexid
 * @author -Wooden-
 */
public class GameServerAuth extends ClientBasePacket {

	private final byte[] _hexId;
	private final int _desiredId;
	private final boolean _hostReserved;
	private final boolean _acceptAlternativeId;
	private final int _maxPlayers;
	private final int port;
	private final String _externalHost;
	private final String _internalHost;
	private final int _serverType;


	public GameServerAuth(byte[] data) {
		super(data);
		_desiredId = readByte();
		_serverType = readInt();
		_acceptAlternativeId = readByte() != 0;
		_hostReserved = readByte() != 0;
		_externalHost = readString();
		_internalHost = readString();
		port = readShort();
		_maxPlayers = readInt();
		int size = readInt();
		_hexId = readBytes(size);
	}

	public byte[] getHexID()
	{
		return _hexId;
	}

	public int getDesiredID()
	{
		return _desiredId;
	}
	
	public boolean acceptAlternateID() {
		return _acceptAlternativeId;
	}

	public int getMaxPlayers() {
		return _maxPlayers;
	}
	
	/**
	 * @return Returns the externalHost.
	 */
	public String getExternalHost()
	{
		return _externalHost;
	}
	
	/**
	 * @return Returns the internalHost.
	 */
	public String getInternalHost()
	{
		return _internalHost;
	}

	public int getPort() {
		return port;
	}

    public int getServerType() {
        return _serverType;
    }
}
