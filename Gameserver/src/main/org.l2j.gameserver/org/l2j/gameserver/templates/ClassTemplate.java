package org.l2j.gameserver.templates;

import org.l2j.gameserver.model.entity.database.CharTemplate;
import org.l2j.gameserver.templates.xml.jaxb.ClassInfo;
import org.l2j.gameserver.templates.xml.jaxb.PlayerTemplate;

public class ClassTemplate extends CharTemplate {

    private ClassInfo classInfo;
    private PlayerTemplate playerTemplate;

    public ClassTemplate(PlayerTemplate playerTemplate, ClassInfo classInfo) {
        super(playerTemplate, classInfo);
        this.playerTemplate = playerTemplate;
        this.classInfo = classInfo;

    }

    @Override
    public Integer getId() {
        return classInfo.getClassId();
    }
}
