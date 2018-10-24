package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("pets")
public class Pets extends Entity<Integer> {

    @Id
    @Column("item_obj_id")
    private int itemObjId;
    private String name;
    private int level;
    private double curHp;
    private double curMp;
    private long exp;
    private long sp;
    private int karma;
    private int pkkills;
    private int fed;

    public Pets() {}

    public Pets( int objectId, String name, int level, double currentHp, double currentMp, long exp, long sp, int karma, int pkKills, int currentFed) {
        this.itemObjId = objectId;
        this.name = name;
        this.level = level;
        this.curHp = currentHp;
        this.curMp = currentMp;
        this.exp = exp;
        this.sp = sp;
        this.karma = karma;
        this.pkkills = pkKills;
        this.fed = currentFed;
    }

    @Override
    public Integer getId() {
        return itemObjId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public double getCurHp() {
        return curHp;
    }

    public double getCurMp() {
        return curMp;
    }

    public long getExp() {
        return exp;
    }

    public long getSp() {
        return sp;
    }

    public int getKarma() {
        return karma;
    }

    public int getPkkills() {
        return pkkills;
    }

    public int getFed() {
        return fed;
    }
}
