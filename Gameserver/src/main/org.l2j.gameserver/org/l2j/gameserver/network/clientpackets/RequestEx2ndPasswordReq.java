package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAckPacket;

import static org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAckPacket.SUCCESS;
import static org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAckPacket.WRONG_PATTERN;

/**
 * (ch)cS{S}
 * c: change pass?
 * S: current password
 * S: new password
 */
public class RequestEx2ndPasswordReq extends L2GameClientPacket {
    private int changePass;
    private String password;
    private String newPassword;

    @Override
    protected void readImpl() {
        changePass = readByte();
        password = readString();
        if (changePass == 2)
            newPassword = readString();
    }

    @Override
    protected void runImpl() {
        boolean success = false;

        if (changePass == 0) {
            success = client.saveSecondFactorPassword(password);
        } else if (changePass == 2) {
            success = client.changeSecondFactorPassword(password, newPassword);
        }

        client.sendPacket(new Ex2ndPasswordAckPacket(success ? SUCCESS : WRONG_PATTERN));

    }
}
