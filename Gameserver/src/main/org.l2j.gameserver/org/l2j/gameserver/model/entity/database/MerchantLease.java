package org.l2j.gameserver.model.entity.database;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.springframework.data.annotation.Id;

@Table("merchant_lease")
public class MerchantLease  {

    @Id
    @Column("merchant_id")
    private int merchantId;
    @Column("player_id")
    private int playerId;
    private int bid;
    private int type;
    @Column("player_name")
    private String playerName;
}
