package org.l2j.gameserver.datatables;

import org.l2j.gameserver.model.base.PlayerClass;
import org.l2j.gameserver.templates.ClassTemplate;
import org.l2j.gameserver.templates.xml.jaxb.ClassInfo;
import org.l2j.gameserver.templates.xml.jaxb.PlayerTemplate;
import org.l2j.gameserver.templates.xml.reader.ClassInfoReader;
import org.l2j.gameserver.templates.xml.reader.PlayerTemplateReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PlayerTemplateTable {
    private static final Logger logger = LoggerFactory.getLogger(PlayerTemplateTable.class);
    private static final int CLASSES_COUNT = 89;
    private PlayerTemplateReader templateReader;
    private Map<Integer, ClassTemplate> templates;

    private static PlayerTemplateTable instance;

    public static PlayerTemplateTable getInstance() {
        if (isNull(instance)) {
            instance = new PlayerTemplateTable();
        }
        return instance;
    }

    private PlayerTemplateTable() {
        templates = new HashMap<>(CLASSES_COUNT);
        loadClassesTemplate();
        logger.info("Loaded {} players Template.", templateReader.getTemplateCount());
        logger.info("Loaded {} classes Template.", templates.size());
    }

    private void loadClassesTemplate() {
        try {
            loadPlayerTemplates();
            loadClassesTemplates();
        } catch (JAXBException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void loadClassesTemplates() throws JAXBException {
        var classTemplateReader = new ClassInfoReader();
        classTemplateReader.readAll();
        classTemplateReader.allInfo().forEach((id, classInfo) -> addToClassTemplate(classTemplateReader, classInfo));
    }

    private PlayerTemplate addToClassTemplate(ClassInfoReader reader, ClassInfo classInfo) {
        PlayerTemplate template;
        if(classInfo.getParent() == -1) {
            template = templateReader.getTemplate(classInfo.getClassId());
        } else {
            template = addToClassTemplate(reader, reader.getClassInfo(classInfo.getParent()));
        }
        if(isNull(template)) {
            System.out.println(template);
        }
        if(!templates.containsKey(classInfo.getClassId()) && nonNull(template)) {
            templates.put(classInfo.getClassId(), new ClassTemplate(template, classInfo));
        }
        return  template;
    }

    private void loadPlayerTemplates() throws JAXBException {
        templateReader = new PlayerTemplateReader();
        templateReader.readAll();
    }

    public ClassTemplate getClassTemplate(int classId){
        return templates.get(classId);
    }

    public PlayerTemplate getPlayerTemplate(PlayerClass playerClass) {
        return getPlayerTemplate(playerClass.getId());
    }

    public PlayerTemplate getPlayerTemplate(int classId) {
        return templateReader.getTemplate(classId);
    }

    public static String getClassNameById(int classId) {
        PlayerTemplate template = getInstance().getPlayerTemplate(classId);
        return template.getName();
    }

    public Collection<PlayerTemplate> allTemplates() {
        return templateReader.getAllTemplates();
    }

    public Collection<ClassTemplate> classTemplates() {
        return templates.values();
    }
}
