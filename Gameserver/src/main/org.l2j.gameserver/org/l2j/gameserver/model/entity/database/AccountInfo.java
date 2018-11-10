package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.model.Entity;
import org.springframework.data.annotation.Id;

@Table("account_info")
public class AccountInfo extends Entity<String> {

    @Column("account")
    private String id;
    @Column("2nd_password")
    private String password;

    public AccountInfo(String account, String password) {
        this.id = account;
        this.password = password;
    }

    @Id
    @Override
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
