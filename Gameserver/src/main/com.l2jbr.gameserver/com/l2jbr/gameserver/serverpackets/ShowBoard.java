package com.l2jbr.gameserver.serverpackets;

import java.util.List;

public class ShowBoard extends L2GameServerPacket
{
	private final String _htmlCode;
	private final String _id;
	private List<String> _arg;
	
	public ShowBoard(String htmlCode, String id)
	{
		_id = id;
		_htmlCode = htmlCode; // html code must not exceed 8192 bytes
	}
	
	public ShowBoard(List<String> arg)
	{
		_id = "1002";
		_htmlCode = null;
		_arg = arg;
		
	}
	
	private byte[] get1002()
	{
		int len = (_id.getBytes().length * 2) + 2;
		for (String arg : _arg)
		{
			len += (arg.getBytes().length + 4) * 2;
		}
		byte data[] = new byte[len];
		int i = 0;
		for (int j = 0; j < _id.getBytes().length; j++, i += 2)
		{
			data[i] = _id.getBytes()[j];
			data[i + 1] = 0;
		}
		data[i] = 8;
		i++;
		data[i] = 0;
		i++;
		for (String arg : _arg)
		{
			for (int j = 0; j < arg.getBytes().length; j++, i += 2)
			{
				data[i] = arg.getBytes()[j];
				data[i + 1] = 0;
			}
			data[i] = 0x20;
			i++;
			data[i] = 0x0;
			i++;
			data[i] = 0x8;
			i++;
			data[i] = 0x0;
			i++;
		}
		return data;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeByte(0x6e);
		writeByte(0x01); // c4 1 to show community 00 to hide
		writeString("bypass _bbshome"); // top
		writeString("bypass _bbsgetfav"); // favorite
		writeString("bypass _bbsloc"); // region
		writeString("bypass _bbsclan"); // clan
		writeString("bypass _bbsmemo"); // memo
		writeString("bypass _bbsmail"); // mail
		writeString("bypass _bbsfriends"); // friends
		writeString("bypass bbs_add_fav"); // add fav.
		if (!_id.equals("1002"))
		{
			// getBytes is a very costy operation, and should only be called once
			byte htmlBytes[] = null;
			if (_htmlCode != null)
			{
				htmlBytes = _htmlCode.getBytes();
			}
			byte data[] = new byte[2 + 2 + 2 + (_id.getBytes().length * 2) + (2 * ((_htmlCode != null) ? htmlBytes.length : 0))];
			int i = 0;
			for (int j = 0; j < _id.getBytes().length; j++, i += 2)
			{
				data[i] = _id.getBytes()[j];
				data[i + 1] = 0;
			}
			data[i] = 8;
			i++;
			data[i] = 0;
			i++;
			if (_htmlCode == null)
			{
				
			}
			else
			{
				for (int j = 0; j < htmlBytes.length; i += 2, j++)
				{
					data[i] = htmlBytes[j];
					data[i + 1] = 0;
				}
			}
			data[i] = 0;
			i++;
			data[i] = 0;
			// writeString(_htmlCode); // current page
			writeBytes(data);
		}
		else
		{
			writeBytes(get1002());
		}
	}
}
