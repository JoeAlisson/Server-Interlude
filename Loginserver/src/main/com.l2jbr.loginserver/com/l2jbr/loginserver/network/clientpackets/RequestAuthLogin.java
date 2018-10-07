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
    private final byte[] _raw = new byte[128];

    @Override
    public boolean readImpl() {
        if (availableData() >= 128) {
            readBytes(_raw);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        byte[] decrypted;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
        } catch (GeneralSecurityException e) {
            logger.warn(e.getLocalizedMessage(), e);
            return;
        }

        String _user = new String(decrypted, 0x5E, 14).trim();
        _user = _user.toLowerCase();
        String _password = new String(decrypted, 0x6C, 16).trim();
        int _ncotp = decrypted[0x7c];
        _ncotp |= decrypted[0x7d] << 8;
        _ncotp |= decrypted[0x7e] << 16;
        _ncotp |= decrypted[0x7f] << 24;

        LoginController lc = LoginController.getInstance();
        AuthLoginResult result = lc.tryAuthLogin(_user, _password, client);

        switch (result) {
            case AUTH_SUCCESS:
                client.setAccount(_user);
                client.setState(LoginClientState.AUTHED_LOGIN);
                client.setSessionKey(lc.assignSessionKeyToClient(_user, client));
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
                if ((oldClient = lc.getAuthedClient(_user)) != null) {
                    // kick the other client
                    oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
                    lc.removeAuthedLoginClient(_user);
                }
                break;
            case ALREADY_ON_GS:
                GameServerInfo gsi;
                if ((gsi = lc.getAccountOnGameServer(_user)) != null) {
                    client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);

                    // kick from there
                    if (gsi.isAuthed()) {
                        gsi.getGameServerThread().kickPlayer(_user);
                    }
                }
                break;
        }
    }
}
