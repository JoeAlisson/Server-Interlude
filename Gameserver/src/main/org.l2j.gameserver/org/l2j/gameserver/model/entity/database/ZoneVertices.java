package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("zone_vertices")
public class ZoneVertices  {
    @Id
    private int id;
    private int order;
    private int x;
    private int y;

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
