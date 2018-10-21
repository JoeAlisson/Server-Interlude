package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Table("random_spawn")
public class RandomSpawn  {

    @Id
    private int groupId;
    private int npcId;
    private int count;
    private int initialDelay;
    private int respawnDelay;
    private int despawnDelay;
    private boolean broadcastSpawn;
    private boolean randomSpawn;
    @Column("groupId")
    private Set<RandomSpawnLoc> locs;

    public int getGroupId() {
        return groupId;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getCount() {
        return count;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public int getDespawnDelay() {
        return despawnDelay;
    }

    public boolean getBroadcastSpawn() {
        return broadcastSpawn;
    }

    public boolean getRandomSpawn() {
        return randomSpawn;
    }

    public Set<RandomSpawnLoc> getLocs() {
        return locs;
    }
}
