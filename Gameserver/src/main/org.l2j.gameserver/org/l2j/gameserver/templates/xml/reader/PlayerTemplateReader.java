package org.l2j.gameserver.templates.xml.reader;

import org.l2j.commons.xml.XMLReader;
import org.l2j.gameserver.templates.xml.jaxb.PlayerTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerTemplateReader extends XMLReader<PlayerTemplate> {

    private static final int TEMPLATE_COUNT = 9;
    Map<Integer, PlayerTemplate> templates;

    public PlayerTemplateReader() throws JAXBException {
        templates = new HashMap<>(TEMPLATE_COUNT);
    }

    public PlayerTemplate getTemplate(int classId) {
        return templates.get(classId);
    }

    public int getTemplateCount() {
        return templates.size();
    }

    public Collection<PlayerTemplate> getAllTemplates() {
        return templates.values();
    }

    @Override
    protected void processEntity(PlayerTemplate entity) {
        templates.put(entity.getClassId(), entity);
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(PlayerTemplate.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return "data/xml/schema/player.xsd";
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[] { "data/xml/player"};
    }
}
