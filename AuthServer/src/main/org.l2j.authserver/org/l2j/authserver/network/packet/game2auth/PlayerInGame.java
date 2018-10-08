package org.l2j.authserver.network.packet.game2auth;

import org.l2j.authserver.network.packet.ClientBasePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author -Wooden-
 */
public class PlayerInGame extends ClientBasePacket {
    private final List<String> _accounts;

    public PlayerInGame(byte[] data) {
        super(data);

        int size = readShort();
        _accounts = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            _accounts.add(readString());
        }
    }

    public List<String> getAccounts() {
        return _accounts;
    }
}