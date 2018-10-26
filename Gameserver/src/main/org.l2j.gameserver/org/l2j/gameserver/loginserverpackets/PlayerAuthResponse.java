package org.l2j.gameserver.loginserverpackets;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends LoginServerBasePacket {
	
	private final String _account;
	private final boolean _authed;

	public PlayerAuthResponse(byte[] decrypt) {
		super(decrypt);
		
		_account = readS();
		_authed = (readC() != 0);
	}

	public String getAccount()
	{
		return _account;
	}

	public boolean isAuthed() {
		return _authed;
	}
	
}