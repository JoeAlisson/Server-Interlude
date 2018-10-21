package org.l2j.gameserver.templates.xml.player;

import org.l2j.commons.xml.XMLReader;
import org.l2j.gameserver.templates.xml.jaxb.ClassInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

public class ClassInfoReader extends XMLReader<ClassInfo> {

    private static final int CLASSES_COUNT = 89;
    private Map<Integer, ClassInfo> classesInfo;

    public ClassInfoReader() throws JAXBException {
        classesInfo = new HashMap<>(CLASSES_COUNT);
    }

    public Map<Integer, ClassInfo> allInfo() {
        return classesInfo;
    }

    public ClassInfo getClassInfo(int classId){
        return classesInfo.get(classId);
    }

    @Override
    protected void processEntity(ClassInfo entity) {
        classesInfo.put(entity.getClassId(), entity);
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ClassInfo.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return "data/xml/stats/classes/classInfo.xsd";
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[] { "data/xml/stats/classes"};
    }
}
