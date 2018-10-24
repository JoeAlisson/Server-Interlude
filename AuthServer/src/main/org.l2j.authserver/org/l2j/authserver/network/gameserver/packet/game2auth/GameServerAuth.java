package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.packet.GameserverReadablePacket;
import org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail;

import java.util.Arrays;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;

public class GameServerAuth extends GameserverReadablePacket {

	private  byte[] hexId;
	private  int desiredId;
	private  boolean hostReserved;
	private  boolean acceptAlternativeId;
	private  int maxPlayers;
	private  int port;
	private  String externalHost;
	private  String internalHost;
	private  int serverType;

	@Override
	protected void readImpl() {
		desiredId = readByte();
		serverType = readInt();
		acceptAlternativeId = readByte() != 0;
		hostReserved = readByte() != 0;
		externalHost = readString();
		internalHost = readString();
		port = readShort();
		maxPlayers = readInt();
		int size = readInt();
		hexId = new byte[size];
		readBytes(hexId);
	}

	@Override
	protected void runImpl()  {
        GameServerManager gameServerManager = GameServerManager.getInstance();
        GameServerInfo gsi = gameServerManager.getRegisteredGameServerById(desiredId);

        if (nonNull(gsi)) {
            if (Arrays.equals(gsi.getHexId(), hexId)) {
                if (gsi.isAuthed()) {
                    client.close(LoginGameServerFail.REASON_ALREADY_LOGGED);
                } else {
                    attachGameServerInfo(gsi, gameServerAuth);
                }
            } else {
                // there is already a server registered with the desired id and different hex id
                // try to registerClient this one with an alternative id
                if (acceptNewGameServerEnabled() && gameServerAuth.acceptAlternateID()) {
                    gsi = new GameServerInfo(id, hexId, this);
                    if (gameServerManager.registerWithFirstAvaliableId(gsi)) {
                        attachGameServerInfo(gsi, gameServerAuth);
                        gameServerManager.registerServerOnDB(gsi);
                    } else {
                        forceClose(LoginGameServerFail.REASON_NO_FREE_ID);
                    }
                } else {
                    forceClose(LoginGameServerFail.REASON_WRONG_HEXID);
                }
            }
        }

	}

    private void handleAuthProcess(GameServerAuth gameServerAuth) {
        GameServerManager gameServerManager = GameServerManager.getInstance();

        int id = gameServerAuth.getDesiredID();
        byte[] hexId = gameServerAuth.getHexID();

        GameServerInfo gsi = gameServerManager.getRegisteredGameServerById(id);
        if (nonNull(gsi)) {
            if (Arrays.equals(gsi.getHexId(), hexId)) {
                if (gsi.isAuthed()) {
                    forceClose(LoginGameServerFail.REASON_ALREADY_LOGGED);
                } else {
                    attachGameServerInfo(gsi, gameServerAuth);
                }
            } else {
                // there is already a server registered with the desired id and different hex id
                // try to registerClient this one with an alternative id
                if (acceptNewGameServerEnabled() && gameServerAuth.acceptAlternateID()) {
                    gsi = new GameServerInfo(id, hexId, this);
                    if (gameServerManager.registerWithFirstAvaliableId(gsi)) {
                        attachGameServerInfo(gsi, gameServerAuth);
                        gameServerManager.registerServerOnDB(gsi);
                    } else {
                        forceClose(LoginGameServerFail.REASON_NO_FREE_ID);
                    }
                } else {
                    forceClose(LoginGameServerFail.REASON_WRONG_HEXID);
                }
            }
        } else {
            if (acceptNewGameServerEnabled()) {
                gsi = new GameServerInfo(id, hexId, this);
                if (gameServerManager.register(id, gsi)) {
                    attachGameServerInfo(gsi, gameServerAuth);
                    gameServerManager.registerServerOnDB(gsi);
                } else {
                    forceClose(LoginGameServerFail.REASON_ID_RESERVED);
                }
            } else {
                forceClose(LoginGameServerFail.REASON_WRONG_HEXID);
            }
        }
    }
}
