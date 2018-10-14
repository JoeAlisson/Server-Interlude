package com.l2jbr.gameserver.clientpackets;

import com.l2jbr.gameserver.LoginServerThread;
import com.l2jbr.gameserver.LoginServerThread.SessionKey;
import com.l2jbr.gameserver.network.SystemMessageId;
import com.l2jbr.gameserver.serverpackets.LoginResult;
import com.l2jbr.gameserver.serverpackets.ServerClose;
import com.l2jbr.gameserver.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class AuthLogin extends L2GameClientPacket {

    private static final String _C__08_AUTHLOGIN = "[C] 08 AuthLogin";
    private static Logger _log = LoggerFactory.getLogger(AuthLogin.class.getName());

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
        if (isNull(client.getAccountName())) {
            client.setAccountName(account);
            var oldClient = LoginServerThread.getInstance().addGameServerLogin(account, client);
            if (nonNull(oldClient)) {
                var loggedPlayer = oldClient.getActiveChar();
                if (nonNull(loggedPlayer)) {
                    loggedPlayer.sendPacket(new SystemMessage(SystemMessageId.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT));
                    loggedPlayer.logout();
                } else {
                    oldClient.close(new ServerClose());
                }
                client.close(LoginResult.ACOUNT_ALREADY_IN_USE);
                LoginServerThread.getInstance().removeServerLogin(account);
            } else {
                LoginServerThread.getInstance().addWaitingClientAndSendRequest(account, client, key);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.l2jbr.gameserver.clientpackets.ClientBasePacket#getType()
     */
    @Override
    public String getType() {
        return _C__08_AUTHLOGIN;
    }
}
