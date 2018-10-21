package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("teleport")
public class Teleport  {

    @Id
    private int id;
    private String Description;
    @Column("loc_x")
    private int locX;
    @Column("loc_y")
    private int locY;
    @Column("loc_z")
    private int locZ;
    private int price;
    private boolean fornoble;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return Description;
    }

    public int getLocX() {
        return locX;
    }

    public int getLocY() {
        return locY;
    }

    public int getLocZ() {
        return locZ;
    }

    public int getPrice() {
        return price;
    }

    public boolean isForNoble() {
        return fornoble;
    }
}
