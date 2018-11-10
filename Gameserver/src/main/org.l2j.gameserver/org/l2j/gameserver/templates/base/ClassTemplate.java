package org.l2j.gameserver.templates.base;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.datatables.PlayerTemplateTable;
import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.templates.xml.jaxb.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassTemplate extends CharTemplate {

    private final int classId;
    private final int parent;
    private final String name;
    private final int level;
    private final Map<Integer, LevelInfo> levelInfo;
    private final PlayerTemplate playerTemplate;

    public ClassTemplate(PlayerTemplate playerTemplate, ClassInfo classInfo) {
        super(playerTemplate);
        this.playerTemplate = playerTemplate;
        this.classId = classInfo.getClassId();
        this.parent = classInfo.getParent();
        this.name = classInfo.getName();
        this.level = classInfo.getClassLevel();
        levelInfo = new HashMap<>(classInfo.getLevel().size());
        classInfo.getLevel().forEach(info -> levelInfo.put(info.getValue(), info));
    }

    @Override
    public Integer getId() {
        return classId;
    }

    public float getHp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getHp(level);
        }
        return levelInfo.get(level).getHp();
    }

    public float getCp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getCp(level);
        }
        return levelInfo.get(level).getCp();
    }

    public float getMp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getMp(level);
        }
        return levelInfo.get(level).getMp();
    }

    public float getHpRegen(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getHpRegen(level);
        }
        return levelInfo.get(level).getHpRegen();
    }

    public float getMpRegen(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getMpRegen(level);
        }
        return levelInfo.get(level).getMpRegen();
    }

    public float getCpRegen(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getMpRegen(level);
        }
        return levelInfo.get(level).getCpRegen();
    }

    public Location getRandomStartingLocation() {
        var locations = playerTemplate.getStartingLocation().getLocation();
        var index = Rnd.get(locations.size());
        return  locations.get(index);
    }

    public Race getRace() {
        return playerTemplate.getRace();
    }

    public List<StartingItem> getStartingItems() {
        return playerTemplate.getStartingItem();
    }

    public String getName() {
        return name;
    }

    public ClassTemplate getParent() {
        if(parent == -1) {
            return null;
        }
        return PlayerTemplateTable.getInstance().getClassTemplate(parent);
    }

    public int getClassLevel() {
        if(parent == -1) {
            return 0;
        }
        return  1 + PlayerTemplateTable.getInstance().getClassTemplate(parent).getClassLevel();
    }

    public float getCollisionRadius(byte sex) {
        var collision = playerTemplate.getCollision();
        return sex == 1 ? collision.getFemaleRadius() : collision.getMaleRadius();
    }

    public float getCollisionHeight(byte sex) {
        var collision = playerTemplate.getCollision();
        return sex == 1 ? collision.getFemaleHeight() : collision.getMaleHeight();
    }

    @Override
    public String toString() {
        return name;
    }
}
