package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.gameserver.serverpackets.VersionCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProtocolVersion extends L2GameClientPacket {
	private static final String _C__00_PROTOCOLVERSION = "[C] 00 ProtocolVersion";
	static Logger logger = LoggerFactory.getLogger(ProtocolVersion.class.getName());
	
	private int _version;
	
	@Override
	protected void readImpl()
	{
		_version = readInt();
	}
	
	@Override
	protected void runImpl() {
		// this packet is never encrypted
		if (_version == -2) {
			logger.info("Ping received");
			// this is just a ping attempt from the new C2 client
			getClient().closeNow();
		} else if ((_version < 60) || (_version > 100)) {
			logger.info("Client: {} -> Protocol Revision: {} is invalid. Minimum is {} and Maximum is {} are supported. Closing connection. ", client, _version, Config.MIN_PROTOCOL_REVISION , Config.MAX_PROTOCOL_REVISION );
			logger.warn("Wrong Protocol Version {}", _version);
			getClient().close(new VersionCheck(client.enableCrypt(), 0));
		} else {
			VersionCheck pk = new VersionCheck(getClient().enableCrypt(), 1);
			getClient().sendPacket(pk);
		}
	}

	@Override
	public String getType()
	{
		return _C__00_PROTOCOLVERSION;
	}
}
