package com.l2jbr.gameserver.serverpackets;

import com.l2jbr.gameserver.model.actor.instance.L2PcInstance;

public class CreatureSay extends L2GameServerPacket
{
	// ddSS
	private static final String _S__4A_CREATURESAY = "[S] 4A CreatureSay";
	private final int _objectId;
	private final int _textType;
	private final String _charName;
	private final String _text;
	
	/**
	 * @param objectId
	 * @param messageType
	 * @param charName
	 * @param text
	 */
	public CreatureSay(int objectId, int messageType, String charName, String text)
	{
		_objectId = objectId;
		_textType = messageType;
		_charName = charName;
		_text = text;
		// setLifeTime(0);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x4a);
		writeInt(_objectId);
		writeInt(_textType);
		writeString(_charName);
		writeString(_text);
		
		L2PcInstance _pci = getClient().getActiveChar();
		if (_pci != null)
		{
			_pci.broadcastSnoop(_textType, _charName, _text);
		}
	}

}
