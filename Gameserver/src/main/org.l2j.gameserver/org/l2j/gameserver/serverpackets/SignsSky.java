package org.l2j.gameserver.serverpackets;

import org.l2j.gameserver.SevenSigns;

/**
 * Changes the sky color depending on the outcome of the Seven Signs competition. packet type id 0xf8 format: c h
 * @author Tempy
 */
public class SignsSky extends L2GameServerPacket
{
	private static int _state = 0;
	
	public SignsSky()
	{
		int compWinner = SevenSigns.getInstance().getCabalHighestScore();
		
		if (SevenSigns.getInstance().isSealValidationPeriod())
		{
			if (compWinner == SevenSigns.CABAL_DAWN)
			{
				_state = 2;
			}
			else if (compWinner == SevenSigns.CABAL_DUSK)
			{
				_state = 1;
			}
		}
	}
	
	public SignsSky(int state)
	{
		_state = state;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0xf8);
		
		if (_state == 2)
		{
			writeShort(258);
		}
		else if (_state == 1)
		{
			writeShort(257);
			// else
			// writeShort(256);
		}
	}
}
