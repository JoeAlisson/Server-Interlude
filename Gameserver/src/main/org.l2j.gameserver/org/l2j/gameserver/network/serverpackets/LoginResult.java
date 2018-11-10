package org.l2j.gameserver.network.serverpackets;

public class LoginResult extends L2GameServerPacket {
    public static int SUCCESS = 0xFFFFFFFF;
    public static int FAILED = 0;
    public static int ACOUNT_ALREADY_IN_USE = 7;
    public static int ACCESS_FAILED_TRY_LATER =  4;
    public static int SYSTEM_ERROR_LOGIN_LATER = 1;
    public static int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = 2;
    public static int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = 3;
    public static int INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = 5;
    public static int ACCESS_FAILED_TRY_LATER2 = 6;
    public static int ACCESS_FAILED_TRY_LATER3 = 8;
    public static int ACCESS_FAILED_TRY_LATER4 = 9;
    public static int ACCESS_FAILED_TRY_LATER5 = 10;

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
}