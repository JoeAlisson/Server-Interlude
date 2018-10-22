package org.l2j.gameserver.templates;

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

    public long getHp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getHp(level);
        }
        return Math.round(levelInfo.get(level).getHp());
    }

    public long getCp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getCp(level);
        }
        return Math.round(levelInfo.get(level).getCp());
    }

    public long getMp(int level) {
        if(level < this.level) {
            return PlayerTemplateTable.getInstance().getClassTemplate(parent).getMp(level);
        }
        return Math.round(levelInfo.get(level).getMp());
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
}
