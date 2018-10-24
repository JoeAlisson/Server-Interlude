package org.l2j.gameserver.datatables;

import org.l2j.gameserver.model.entity.database.repository.CharacterRepository;

import static org.l2j.commons.database.DatabaseAccess.getRepository;

public class CharNameTable {

    private CharNameTable() {
    }

    public static boolean doesCharNameExist(String name) {
        return getRepository(CharacterRepository.class).existsByName(name);
    }

    public static int accountCharNumber(String account) {
        return getRepository(CharacterRepository.class).countByAccount(account);
    }
}
