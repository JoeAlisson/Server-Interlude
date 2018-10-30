package com.l2jb.xml;

import com.l2jb.xml.generated.ItemList;
import org.l2j.commons.xml.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class NewXmlReader extends XMLReader<ItemList> {

    public NewXmlReader() throws JAXBException {
    }

    @Override
    protected void processEntity(ItemList entity) {

    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ItemList.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return null;
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[0];
    }
}
