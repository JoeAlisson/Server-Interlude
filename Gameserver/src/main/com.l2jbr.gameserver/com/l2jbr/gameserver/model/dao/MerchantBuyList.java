package com.l2jbr.gameserver.model.dao;

import com.l2jbr.commons.database.dao.DAO;

public class MerchantBuyList implements DAO {

    private int item_id;
    private int price;
    private int shop_id;
    private int order;
    private int count;
    private int currentCount;
    private int time;
    private long savetimer;
}
