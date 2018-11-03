package org.l2j.xml;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Verifier {

    private static GeneratedXmlReader reader;

    public static void verify() throws JAXBException, IOException {
        reader = new GeneratedXmlReader();
        reader.readAll();
        var items = reader.getItems();
        System.out.println("loaded converted items: " + items.size());
        var onClient = Files.readAllLines(Paths.get("itemname_classic-eu.csv"));
        for (String str : onClient) {
            if(!items.containsKey(Integer.parseInt(str))) {
                System.out.println("Missing Item " + str);
            }
        }

    }
}
