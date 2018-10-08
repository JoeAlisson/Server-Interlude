package org.l2j.authserver.network.gameserverpackets;

import org.l2j.authserver.GameServerManager;
import org.l2j.authserver.network.clientpackets.ClientBasePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * @author -Wooden-
 */
public class ServerStatus extends ClientBasePacket {

    protected static Logger _log = LoggerFactory.getLogger(ServerStatus.class);

	private static final int SERVER_LIST_STATUS = 0x01;
	private static final int SERVER_LIST_CLOCK = 0x02;
	private static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
	private static final int MAX_PLAYERS = 0x04;
	private static final int TEST_SERVER = 0x05;
	
	public static final int STATUS_AUTO = 0x00;
	public static final int STATUS_GOOD = 0x01;
	public static final int STATUS_NORMAL = 0x02;
	public static final int STATUS_FULL = 0x03;
	public static final int STATUS_DOWN = 0x04;
	public static final int STATUS_GM_ONLY = 0x05;
	
	private static final int ON = 0x01;

	public ServerStatus(byte[] data, int serverId) {
		super(data);
		
		GameServerManager.GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServerById(serverId);
		if (nonNull(gsi)) {
			int size = readInt();
			for (int i = 0; i < size; i++) {
				int type = readInt();
				int value = readInt();

				switch (type) {
					case SERVER_LIST_STATUS:
						gsi.setStatus(value);
						break;
					case SERVER_LIST_CLOCK:
						gsi.setShowingClock(value == ON);
						break;
					case SERVER_LIST_SQUARE_BRACKET:
						gsi.setShowingBrackets(value == ON);
						break;
					case TEST_SERVER:
						gsi.setTestServer(value == ON);
						break;
					case MAX_PLAYERS:
						gsi.setMaxPlayers(value);
						break;
				}
			}
		}
	}
}