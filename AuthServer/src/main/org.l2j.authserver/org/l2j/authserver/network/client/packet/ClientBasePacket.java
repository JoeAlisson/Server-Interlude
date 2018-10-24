package org.l2j.authserver.network.client.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static java.lang.System.arraycopy;

public abstract class ClientBasePacket {

	private static final Logger logger = LoggerFactory.getLogger(ClientBasePacket.class);
	private final byte[] data;
	private int _off;
	
	public ClientBasePacket(byte[] data) {
		this.data = data;
		_off = 1; // skip packet type id
	}
	
	protected int readInt() {
		int result = data[_off++] & 0xff;
		result |= (data[_off++] << 8) & 0xff00;
		result |= (data[_off++] << 0x10) & 0xff0000;
		result |= (data[_off++] << 0x18) & 0xff000000;
		return result;
	}
	
	protected int readByte() {
		return data[_off++] & 0xff;
	}
	
	protected int readShort() {
		int result = data[_off++] & 0xff;
		result |= (data[_off++] << 8) & 0xff00;
		return result;
	}
	
	public double readDouble()
	{
		long result = readLong();
		return Double.longBitsToDouble(result);
	}

	protected long readLong() {
		long result = data[_off++] & 0xff;
		result |= (data[_off++] << 8) & 0xff00;
		result |= (data[_off++] << 0x10) & 0xff0000;
		result |= (data[_off++] << 0x18) & 0xff000000;
		result |= ((long) data[_off++] << 0x20) & 0xff00000000L;
		result |= ((long) data[_off++] << 0x28) & 0xff0000000000L;
		result |= ((long) data[_off++] << 0x30) & 0xff000000000000L;
		result |= ((long) data[_off++] << 0x38) & 0xff00000000000000L;
		return result;
	}
	
	protected String readString()
	{
		String result = "";
		try {
			result = new String(data, _off, data.length - _off, StandardCharsets.UTF_16LE);
			result = result.substring(0, result.indexOf(0x00));
			_off += (result.length() * 2) + 2;
		} catch (Exception e) {
		    logger.error(e.getLocalizedMessage(), e);
		}
		return result;
	}
	
	protected final byte[] readBytes(int length) {
		byte[] result = new byte[length];
        arraycopy(data, _off, result, 0, length);
		_off += length;
		return result;
	}
}
