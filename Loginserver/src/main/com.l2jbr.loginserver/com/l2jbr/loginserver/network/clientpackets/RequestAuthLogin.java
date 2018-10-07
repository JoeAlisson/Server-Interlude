package com.l2jbr.loginserver.network.clientpackets;

import com.l2jbr.commons.Config;
import com.l2jbr.loginserver.GameServerTable.GameServerInfo;
import com.l2jbr.loginserver.network.L2LoginClient;
import com.l2jbr.loginserver.network.L2LoginClient.LoginClientState;
import com.l2jbr.loginserver.network.LoginController;
import com.l2jbr.loginserver.network.LoginController.AuthLoginResult;
import com.l2jbr.loginserver.network.serverpackets.AccountKicked;
import com.l2jbr.loginserver.network.serverpackets.AccountKicked.AccountKickedReason;
import com.l2jbr.loginserver.network.serverpackets.LoginFail.LoginFailReason;
import com.l2jbr.loginserver.network.serverpackets.LoginOk;
import com.l2jbr.loginserver.network.serverpackets.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket {
    private static final Logger logger = LoggerFactory.getLogger(RequestAuthLogin.class);
    private final byte[] userData = new byte[128];
    private final byte[] authData = new byte[128];
    private boolean useNewAuth;

    @Override
    public boolean readImpl() {
        if (availableData() >= 255) {
            useNewAuth = true;
            readBytes(userData);
            readBytes(authData);
            return true;
        }

        if(availableData() >= 128) {
            readBytes(userData);
            readInt(); // sessionId
            readInt(); // GG
            readInt(); // GG
            readInt(); // GG
            readInt(); // GG
            readInt(); // Game Id ?
            readShort();
            readByte();
            byte[] unk = new byte[16];
            readBytes(unk);
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        byte[] decUserData;
        byte[] decAuthData = null;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decUserData = rsaCipher.doFinal(userData, 0x00, 0x80);

            if(useNewAuth) {
                decAuthData =  rsaCipher.doFinal(authData, 0x00, 0x80);;
            }

        } catch (Exception e) {
            client.close(LoginFailReason.REASON_SYSTEM_ERROR);
            logger.warn(e.getLocalizedMessage(), e);
            return;
        }

        String user;
        String password;
        if(useNewAuth) {
            user = new String(decUserData, 0x4E, 32).trim().toLowerCase();
            password = new String(decAuthData, 0x5C, 16).trim();
        } else {
            user = new String(decUserData, 0x5E, 14).trim().toLowerCase();
            password = new String(decUserData, 0x6C, 16).trim();
        }


        LoginController lc = LoginController.getInstance();
        AuthLoginResult result = lc.tryAuthLogin(user, password, client);

        switch (result) {
            case AUTH_SUCCESS:
                client.setAccount(user);
                client.setState(LoginClientState.AUTHED_LOGIN);
                client.setSessionKey(lc.assignSessionKeyToClient(user, client));
                if (Config.SHOW_LICENCE) {
                    client.sendPacket(new LoginOk(getClient().getSessionKey()));
                } else {
                    getClient().sendPacket(new ServerList());
                }
                break;
            case INVALID_PASSWORD:
                client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
                break;
            case ACCOUNT_BANNED:
                client.close(new AccountKicked(AccountKickedReason.REASON_PERMANENTLY_BANNED));
                break;
            case ALREADY_ON_LS:
                L2LoginClient oldClient;
                if ((oldClient = lc.getAuthedClient(user)) != null) {
                    // kick the other client
                    oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
                    lc.removeAuthedLoginClient(user);
                }
                break;
            case ALREADY_ON_GS:
                GameServerInfo gsi;
                if ((gsi = lc.getAccountOnGameServer(user)) != null) {
                    client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);

                    // kick from there
                    if (gsi.isAuthed()) {
                        gsi.getGameServerThread().kickPlayer(user);
                    }
                }
                break;
        }
    }
}
