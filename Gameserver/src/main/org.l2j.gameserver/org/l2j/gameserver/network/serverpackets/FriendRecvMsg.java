package org.l2j.gameserver.network.serverpackets;

/**
 * Send Private (Friend) Message Format: c dSSS d: Unknown S: Sending Player S: Receiving Player S: Message
 * @author Tempy
 */
public class FriendRecvMsg extends L2GameServerPacket
{
	private final String _sender, _receiver, _message;
	
	public FriendRecvMsg(String sender, String reciever, String message)
	{
		_sender = sender;
		_receiver = reciever;
		
		_message = message;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xfd);
		
		writeInt(0); // ??
		writeString(_receiver);
		writeString(_sender);
		writeString(_message);
	}
	
}
