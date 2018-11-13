package org.l2j.gameserver.network.clientpackets;


import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerifyPacket;

import static org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerifyPacket.PASSWORD_BAN;
import static org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerifyPacket.PASSWORD_OK;
import static org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerifyPacket.PASSWORD_WRONG;

public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	private String password;

	@Override
	protected void readImpl()
	{
		password = readString();
	}

	@Override
	protected void runImpl() {
		var wrongAttemps = client.verifySecondFactorPassword(password);

		if(wrongAttemps == 0) {
		    sendPacket(new Ex2ndPasswordVerifyPacket(PASSWORD_OK, wrongAttemps));
        } else if(wrongAttemps > 5) { // TODO implements Configuration Max Attemps, Ban
            sendPacket(new Ex2ndPasswordVerifyPacket(PASSWORD_BAN, wrongAttemps));
        } else {
            sendPacket(new Ex2ndPasswordVerifyPacket(PASSWORD_WRONG, wrongAttemps));
        }
	}
}
