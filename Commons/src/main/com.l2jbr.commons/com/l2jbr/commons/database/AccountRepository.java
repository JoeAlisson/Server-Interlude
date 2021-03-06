package com.l2jbr.commons.database;

import com.l2jbr.commons.database.model.Account;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, String> {

    @Modifying
    @Query("REPLACE accounts (login, password, access_level) values (:login, :password, :accessLevel)")
    int createOrUpdateAccount(@Param("login") String login, @Param("password") String password, @Param("accessLevel") short accessLevel);

    @Modifying
    @Query("UPDATE accounts SET access_level=:accessLevel WHERE login=:login")
    int updateAccessLevel(@Param("login") String login, @Param("accessLevel") int acessLevel);

    @Modifying
    @Query("UPDATE accounts SET lastServer=:server WHERE login=:login")
    int updateLastServer(@Param("login") String login, @Param("server") int server);

    @Query("SELECT * FROM accounts WHERE login=:login")
    Optional<Account> findByLogin(@Param("login") String login);

}

