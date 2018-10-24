package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("clan_wars")
public class ClanWars extends Entity<Integer> {

    @Id
    private int clan1;
    private int clan2;
    private int wantspeace1;
    private int wantspeace2;

    @Override
    public Integer getId() {
        return clan1;
    }

    public int getClan1() {
        return clan1;
    }

    public int getClan2() {
        return clan2;
    }

    public int getWantspeace1() {
        return wantspeace1;
    }

    public int getWantspeace2() {
        return wantspeace2;
    }
}
