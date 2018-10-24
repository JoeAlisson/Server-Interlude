package org.l2j.gameserver.factory;

import org.l2j.gameserver.model.L2ItemInstance;

public class ItemFactory {

    public static L2ItemInstance create(int itemTemplateId) {
        var objectId =  IdFactory.getInstance().getNextId();
        var item = new L2ItemInstance(objectId, itemTemplateId);
        return item;
    }

    public static void releaseId(int objectId) {
        IdFactory.getInstance().releaseId(objectId);
    }
}
