package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.AuthServerClient;
import org.l2j.gameserver.AuthServerClient.SessionKey;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.LoginResult;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.LoginResult.ACOUNT_ALREADY_IN_USE;
import static org.l2j.gameserver.network.serverpackets.LoginResult.FAILED;

public final class AuthLogin extends L2GameClientPacket {

    // account + keys must match what the loginserver used.
    private String account;

    private int sessionId;
    private int accountId;
    private int authAccountId;
    private int authKey;
    private int localization;

    @Override
    protected void readImpl() {
        account = readString().toLowerCase();
        accountId = readInt();
        sessionId = readInt();
        authAccountId = readInt();
        authKey = readInt();
        localization = readInt();
    }

    @Override
    protected void runImpl() {
        SessionKey key = new SessionKey(authAccountId, authKey, sessionId, accountId);
        // avoid potential exploits
        if (isNull(client.getAccount())) {
            client.setAccount(account);
            var oldClient = AuthServerClient.getInstance().addGameServerLogin(account, client);
            if (nonNull(oldClient)) {
                var loggedPlayer = oldClient.getActiveChar();
                if (nonNull(loggedPlayer)) {
                    loggedPlayer.sendPacket(new SystemMessage(SystemMessageId.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT));
                    loggedPlayer.logout();
                } else {
                    oldClient.close(new ServerClose());
                }
                client.close(new LoginResult(FAILED, ACOUNT_ALREADY_IN_USE));
                AuthServerClient.getInstance().removeServerLogin(account);
            } else {
                AuthServerClient.getInstance().addWaitingClientAndSendRequest(account, client, key);
            }
        }
    }
}
