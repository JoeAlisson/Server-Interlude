package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("clan_privs")
public class ClanPrivs extends Entity<Integer> {

    @Id
    @Column("clan_id")
    private int clanId;
    private int rank;
    private int party;
    private int privs;

    @Override
    public Integer getId() {
        return clanId;
    }

    public int getRank() {
        return rank;
    }

    public int getParty() {
        return party;
    }

    public int getPrivs() {
        return privs;
    }


}
