package com.l2jb.xml;

import com.l2jb.xml.old.generated.ItemType;
import com.l2jb.xml.old.generated.List;
import org.l2j.commons.xml.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class OldXmlReader extends XMLReader<List> {

    Map<BigInteger, ItemType> items;

    public OldXmlReader() throws JAXBException {
    }

    @Override
    protected void processEntity(List entity) {
        items = new HashMap<>();
        for (ItemType itemType : entity.getItem()) {
            items.put(itemType.getId(), itemType);
            itemType.
        }
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(List.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return "schema/items.xsd";
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[] { "xml" };
    }
}
