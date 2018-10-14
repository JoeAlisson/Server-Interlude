package com.l2jbr.gameserver.serverpackets;

public class LoginResult extends L2GameServerPacket {
    public static L2GameServerPacket SUCCESS = new LoginResult(0xFFFFFFFF, 0);
    public static L2GameServerPacket SYSTEM_ERROR_LOGIN_LATER = new LoginResult(0, 1);
    public static L2GameServerPacket PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = new LoginResult(0, 2);
    public static L2GameServerPacket PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = new LoginResult(0, 3);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER = new LoginResult(0, 4);
    public static L2GameServerPacket INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = new LoginResult(0, 5);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER2 = new LoginResult(0, 6);
    public static L2GameServerPacket ACOUNT_ALREADY_IN_USE = new LoginResult(0, 7);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER3 = new LoginResult(0, 8);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER4 = new LoginResult(0, 9);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER5 = new LoginResult(0, 10);

    private final int _reason1;
    private final int _reason2;

    public LoginResult(int reason1, int reason2) {
        _reason1 = reason1;
        _reason2 = reason2;
    }

    @Override
    protected final void writeImpl() {
        writeByte(0x0A);
        writeInt(_reason1);
        writeInt(_reason2);
    }

    @Override
    public String getType() {
        return "0x0A_LOGIN_RESULT";
    }
}