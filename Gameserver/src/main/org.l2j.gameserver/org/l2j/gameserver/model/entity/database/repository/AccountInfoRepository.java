package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.AccountInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AccountInfoRepository extends CrudRepository<AccountInfo, String> {

    @Query("SELECT EXISTS (SELECT 1 FROM account_info WHERE account=:account AND 2nd_password IS NOT NULL)")
    boolean hasPassword(@Param("account") String account);
}
