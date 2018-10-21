package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("characters")
public class Character extends Entity<Integer> {

    @Id
    @Column("object_id")
    private int objectId;
    @Column("account")
    private String account;
    @Column("char_name")
    private String name;
    private Byte level;
    private long hp;
    private long cp;
    private long mp;
    private Byte face;
    @Column("hair_style")
    private Byte hairStyle;
    @Column("hair_color")
    private Byte hairColor;
    private int sex;
    private int heading;
    private int x;
    private int y;
    private int z;
    private long exp;
    @Column("exp_before_death")
    private long expBeforeDeath;
    private long sp;
    private int karma;
    private int pvp;
    private int pk;
    private int clan;
    private int race;
    @Column("class_id")
    private int classId;
    @Column("base_class")
    private int baseClass;
    @Column("delete_time")
    private long deleteTime;
    private String title;
    @Column("rec_have")
    private int recHave;
    @Column("rec_left")
    private int recLeft;
    @Column("access_level")
    private int accesslevel;
    private boolean online;
    @Column("online_time")
    private long onlineTime;
    private byte slot;
    private boolean newbie;
    @Column("last_access")
    private long lastAccess;
    @Column("clan_privs")
    private int clanPrivs;
    private boolean wantspeace;
    @Column("in_seven_signs")
    private boolean inSevenSigns;
    @Column("in_jail")
    private boolean inJail;
    @Column("jail_timer")
    private long jailTimer;
    @Column("power_grade")
    private int powerGrade;
    private boolean nobless;
    private byte subpledge;
    @Column("last_recom_date")
    private long lastRecomDate;
    @Column("lvl_joined_academy")
    private short lvlJoinedAcademy;
    private int apprentice;
    private int sponsor;
    @Column("varka_ketra_ally")
    private int varkaKetraAlly;
    @Column("clan_join_expiry_time")
    private long clanJoinExpiryTime;
    @Column("clan_create_expiry_time")
    private long clanCreateExpiryTime;
    @Column("death_penalty_level")
    private int deathPenaltyLevel;

    @Override
    public Integer getId() {
        return objectId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getCharName() {
        return name;
    }

    public void setCharName(String char_name) {
        this.name = char_name;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public long getCurrentHp() {
        return hp;
    }

    public void setCurrentHp(long curHp) {
        this.hp = curHp;
    }

    public long getCp() {
        return cp;
    }

    public void setCurrentCp(long curCp) {
        this.cp = curCp;
    }

    public long getCurrentMp() {
        return mp;
    }

    public void setCurrentMp(long curMp) {
        this.mp = curMp;
    }

    public byte getFace() {
        return face;
    }

    public void setFace(byte face) {
        this.face = face;
    }

    public byte getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(byte hairStyle) {
        this.hairStyle = hairStyle;
    }

    public byte getHairColor() {
        return hairColor;
    }

    public void setHairColor(byte hairColor) {
        this.hairColor = hairColor;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getExperience() {
        return exp;
    }

    public void setExperience(long exp) {
        this.exp = exp;
    }

    public long getExpBeforeDeath() {
        return expBeforeDeath;
    }

    public void setExpBeforeDeath(long expBeforeDeath) {
        this.expBeforeDeath = expBeforeDeath;
    }

    public long getSp() {
        return sp;
    }

    public void setSkillPoint(int sp) {
        this.sp = sp;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getPvp() {
        return pvp;
    }

    public void setPvp(int pvp) {
        this.pvp = pvp;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getClanId() {
        return clan;
    }

    public void setClan(int clan) {
        this.clan = clan;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(int baseClass) {
        this.baseClass = baseClass;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRecHave() {
        return recHave;
    }

    public void setRecommendHave(int recHave) {
        this.recHave = recHave;
    }

    public int getRecLeft() {
        return recLeft;
    }

    public void setRecommendLeft(int recLeft) {
        this.recLeft = recLeft;
    }

    public int getAccesslevel() {
        return accesslevel;
    }

    public void setAccesslevel(int accesslevel) {
        this.accesslevel = accesslevel;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }

    public boolean isNewbie() {
        return newbie;
    }

    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setClanPrivileges(int clanPrivs) {
        this.clanPrivs = clanPrivs;
    }

    public boolean wantsPeace() {
        return wantspeace;
    }

    public void setWantspeace(boolean wantspeace) {
        this.wantspeace = wantspeace;
    }

    public boolean isInSevenSigns() {
        return inSevenSigns;
    }

    public void setInSevenSigns(boolean isInSevenSigns) {
        this.inSevenSigns = isInSevenSigns;
    }

    public boolean isInJail() {
        return inJail;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

    public long getJailTimer() {
        return jailTimer;
    }

    public void setJailTimer(long jailTimer) {
        this.jailTimer = jailTimer;
    }

    public int getPowerGrade() {
        return powerGrade;
    }

    public void setPowerGrade(int powerGrade) {
        this.powerGrade = powerGrade;
    }

    public boolean isNobless() {
        return nobless;
    }

    public void setNobless(boolean nobless) {
        this.nobless = nobless;
    }

    public byte getSubpledge() {
        return subpledge;
    }

    public void setSubpledge(byte subpledge) {
        this.subpledge = subpledge;
    }

    public long getLastRecomDate() {
        return lastRecomDate;
    }

    public void setLastRecommendDate(long lastRecomDate) {
        this.lastRecomDate = lastRecomDate;
    }

    public short getLvlJoinedAcademy() {
        return lvlJoinedAcademy;
    }

    public void setLvlJoinedAcademy(short lvlJoinedAcademy) {
        this.lvlJoinedAcademy = lvlJoinedAcademy;
    }

    public int getApprentice() {
        return apprentice;
    }

    public void setApprentice(int apprentice) {
        this.apprentice = apprentice;
    }

    public int getSponsor() {
        return sponsor;
    }

    public void setSponsor(int sponsor) {
        this.sponsor = sponsor;
    }

    public int getVarkaKetraAlly() {
        return varkaKetraAlly;
    }

    public void setVarkaKetraAlly(int varkaKetraAlly) {
        this.varkaKetraAlly = varkaKetraAlly;
    }

    public long getClanJoinExpiryTime() {
        return clanJoinExpiryTime;
    }

    public void setClanJoinExpiryTime(long clanJoinExpiryTime) {
        this.clanJoinExpiryTime = clanJoinExpiryTime;
    }

    public long getClanCreateExpiryTime() {
        return clanCreateExpiryTime;
    }

    public void setClanCreateExpiryTime(long clanCreateExpiryTime) {
        this.clanCreateExpiryTime = clanCreateExpiryTime;
    }

    public int getDeathPenaltyLevel() {
        return deathPenaltyLevel;
    }

    public void setDeathPenaltyLevel(int deathPenaltyLevel) {
        this.deathPenaltyLevel = deathPenaltyLevel;
    }

    public void updateLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void clearClanData(long clanJoinExpiryTime, long clanCreateExpiryTime) {
        clan = 0;
        title = "";
        this.clanJoinExpiryTime = clanJoinExpiryTime;
        this.clanCreateExpiryTime = clanCreateExpiryTime;
        clanPrivs = 0;
        wantspeace = false;
        subpledge = 0;
        lvlJoinedAcademy = 0;
        apprentice = 0;
        sponsor = 0;
    }
}
