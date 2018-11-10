
/**
 * @author FBIagent
 */
package org.l2j.gameserver.datatables;

import org.l2j.gameserver.model.L2ExtractableItem;
import org.l2j.gameserver.model.L2ExtractableProductItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static java.util.Objects.isNull;

public class ExtractableItemsData {

    private static final Logger logger = LoggerFactory.getLogger(ExtractableItemsData.class);
    private static final String EXTRACTABLE_ITEMS_FILE = "./data/extractable_items.csv";

    private HashMap<Integer, L2ExtractableItem> _items;

    private static ExtractableItemsData INSTANCE = null;

    public static ExtractableItemsData getInstance() {
        if (isNull(INSTANCE )) {
            INSTANCE = new ExtractableItemsData();
        }
        return INSTANCE;
    }

    public ExtractableItemsData() {
        _items = new HashMap<>();

        Scanner s;

        try {
            s = new Scanner(new File(EXTRACTABLE_ITEMS_FILE));
        } catch (Exception e) {
            logger.error("Extractable items data: Can not find {}", EXTRACTABLE_ITEMS_FILE);
            return;
        }

        int lineCount = 0;

        while (s.hasNextLine()) {
            lineCount++;

            String line = s.nextLine();

            if (line.startsWith("#")) {
                continue;
            } else if (line.equals("")) {
                continue;
            }

            String[] lineSplit = line.split(";");
            boolean ok = true;
            int itemID = 0;

            try {
                itemID = Integer.parseInt(lineSplit[0]);
            } catch (Exception e) {
                logger.warn("Extractable items data: Error in line {} -> invalid item id or wrong seperator after item id! \n {}", lineCount, line);
                ok = false;
            }

            if (!ok) {
                continue;
            }

            List<L2ExtractableProductItem> product_temp = new LinkedList<>();

            for (int i = 0; i < (lineSplit.length - 1); i++) {
                ok = true;

                String[] lineSplit2 = lineSplit[i + 1].split(",");

                if (lineSplit2.length != 3) {
                    System.out.println("Extractable items data: Error in line " + lineCount + " -> wrong seperator!");
                    System.out.println("		" + line);
                    ok = false;
                }

                if (!ok) {
                    continue;
                }

                int production = 0, amount = 0, chance = 0;

                try {
                    production = Integer.parseInt(lineSplit2[0]);
                    amount = Integer.parseInt(lineSplit2[1]);
                    chance = Integer.parseInt(lineSplit2[2]);
                } catch (Exception e) {
                    System.out.println("Extractable items data: Error in line " + lineCount + " -> incomplete/invalid production data or wrong seperator!");
                    System.out.println("		" + line);
                    ok = false;
                }

                if (!ok) {
                    continue;
                }

                L2ExtractableProductItem product = new L2ExtractableProductItem(production, amount, chance);
                product_temp.add(product);
            }

            int fullChances = 0;

            for (L2ExtractableProductItem Pi : product_temp) {
                fullChances += Pi.getChance();
            }

            if (fullChances > 100) {
                logger.warn("Extractable items data: Error in line {} -> allTemplates chances together are more then 100!.\n{}", lineCount, line);
                continue;
            }
            L2ExtractableItem product = new L2ExtractableItem(itemID, product_temp);
            _items.put(itemID, product);
        }

        s.close();
        logger.info("Loaded {} extractable items!", _items.size() );

    }

    public L2ExtractableItem getExtractableItem(int itemID) {
        return _items.get(itemID);
    }

    public int[] itemIDs() {
        int size = _items.size();
        int[] result = new int[size];
        int i = 0;
        for (L2ExtractableItem ei : _items.values()) {
            result[i] = ei.getItemId();
            i++;
        }
        return result;
    }
}
