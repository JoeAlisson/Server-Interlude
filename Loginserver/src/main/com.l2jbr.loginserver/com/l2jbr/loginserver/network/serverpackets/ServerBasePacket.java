package com.l2jbr.loginserver.network.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;

public abstract class ServerBasePacket  {

    private static final Logger logger = LoggerFactory.getLogger(ServerBasePacket.class);

    ByteArrayOutputStream _bao;


	protected ServerBasePacket()
	{
		_bao = new ByteArrayOutputStream();
	}
	
	protected void writeInt(int value) {
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
		_bao.write((value >> 16) & 0xff);
		_bao.write((value >> 24) & 0xff);
	}
	
	protected void writeH(int value)
	{
		_bao.write(value & 0xff);
		_bao.write((value >> 8) & 0xff);
	}
	
	protected void writeByte(int value) {
		_bao.write(value & 0xff);
	}
	
	protected void writeF(double org)
	{
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) ((value >> 8) & 0xff));
		_bao.write((int) ((value >> 16) & 0xff));
		_bao.write((int) ((value >> 24) & 0xff));
		_bao.write((int) ((value >> 32) & 0xff));
		_bao.write((int) ((value >> 40) & 0xff));
		_bao.write((int) ((value >> 48) & 0xff));
		_bao.write((int) ((value >> 56) & 0xff));
	}
	
	protected void writeString(String text) {
		try {
			if (nonNull(text)) {
				_bao.write(text.getBytes(StandardCharsets.UTF_16LE));
			}
		} catch (Exception e) {
		    logger.error(e.getLocalizedMessage(), e);
		}
		
		_bao.write(0);
		_bao.write(0);
	}
	
	protected void writeBytes(byte[] array) {
		try {
			_bao.write(array);
		}
		catch (IOException e) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	public int getLength()
	{
		return _bao.size() + 2;
	}
	
	public byte[] getBytes()
	{
		// if (this instanceof Init)
		// writeInt(0x00); //reserve for XOR initial key
		
		writeInt(0x00); // reserve for checksum
		
		int padding = _bao.size() % 8;
		if (padding != 0)
		{
			for (int i = padding; i < 8; i++)
			{
				writeByte(0x00);
			}
		}
		
		return _bao.toByteArray();
	}
	
	public abstract byte[] getContent() throws IOException;
}
