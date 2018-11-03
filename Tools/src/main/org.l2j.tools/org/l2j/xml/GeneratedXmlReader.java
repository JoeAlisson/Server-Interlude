package org.l2j.xml;

import org.l2j.commons.xml.XMLReader;
import org.l2j.xml.generated.ItemList;
import org.l2j.xml.generated.ItemTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

public class GeneratedXmlReader extends XMLReader<ItemList> {

    Map<Integer, ItemTemplate> items;

    public GeneratedXmlReader() throws JAXBException {
        items = new HashMap<>();
    }

    @Override
    protected void processEntity(ItemList entity) {
        for (JAXBElement<? extends ItemTemplate> jaxbElement : entity.getItemTemplate()) {
            var value = jaxbElement.getValue();
            items.put(value.getId(), value);
        }
    }

    public Map<Integer, ItemTemplate> getItems() {
        return items;
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(ItemList.class);
    }

    @Override
    protected String getSchemaFilePath() {
        return "schema/item.xsd";
    }

    @Override
    protected String[] getXmlFileDirectories() {
        return new String[] { "result" };
    }
}
