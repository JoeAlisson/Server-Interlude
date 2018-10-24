package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.Character;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CharacterRepository extends CrudRepository<Character, Integer> {

    @Query("SELECT * FROM characters WHERE name=:charName")
    Optional<Character> findByCharName(@Param("charName") String charName);

    @Modifying
    @Query("UPDATE characters SET access_level=:accessLevel WHERE name=:name")
    int updateAccessLevelByCharName(@Param("name") String charName, @Param("accessLevel") int accessLevel);

    @Query("SELECT * FROM characters WHERE account=:account AND object_id<>:objectId")
    Iterable<Character> findOthersCharactersOnAccount(@Param("account") String accountName, @Param("objectId") int objectId);

    @Modifying
    @Query("UPDATE characters SET delete_time=:deleteTime WHERE object_id=:objectId")
    int updateDeleteTime(@Param("objectId") int objectId, @Param("deleteTime") long deleteTime);

    @Query("SELECT * FROM characters WHERE account=:account")
    Iterable<Character> findAllByAccountName(@Param("account") String account);

    @Modifying
    @Query("UPDATE characters SET clan=0 WHERE clan=:clanId")
    int removeClanId(@Param("clanId") int clanId);

    @Query("SELECT * FROM characters WHERE clan=:clanId")
    Iterable<Character> findAllByClanId(@Param("clanId") int clanId);

    @Query("SELECT account FROM characters WHERE name=:name")
    String findAccountByName(@Param("name") String charName);

    @Query("SELECT EXISTS (SELECT 1 FROM characters WHERE name=:name LIMIT 1)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(1) FROM characters WHERE account=:account")
    int countByAccount(@Param("account") String account);

    @Query("SELECT clan FROM characters WHERE object_id =:objectId")
    int findClanIdById(@Param("objectId") int charId);

    @Modifying
    @Query("UPDATE characters SET x=:x, y=:y, z=:z, in_jail=:inJail, jail_timer=:jailTime WHERE name=:name")
    int updateJailStatusByName(@Param("name") String charName, @Param("x") int x, @Param("y") int y, @Param("z") int z,
                                @Param("inJail") int inJail, @Param("jailTime") long jailTime);

    @Modifying
    @Query("UPDATE characters SET apprentice=0 WHERE apprentice=:apprenticeId")
    int removeApprentice(@Param("apprenticeId") int apprenticeId);

    @Modifying
    @Query("UPDATE characters SET sponsor=0 WHERE sponsor=:sponsorId")
    int removeSponsor(@Param("sponsorId") int sponsorId);

    @Modifying
    @Query("UPDATE characters SET karma=:karma, pk=:pkKills WHERE object_id=:objectId")
    int updatePKAndKarma(@Param("objectId") int objectId, @Param("pkKills") int playerPkKills, @Param("karma") int playerKarma);

    @Modifying
    @Query("UPDATE characters SET subpledge=:subpledge WHERE object_id=:objectId")
    void updateSubpledge(@Param("objectId") int objectId, @Param("subpledge") int subpledge);

    @Modifying
    @Query("UPDATE characters SET online=:online, last_access=:lastAccess WHERE object_id=:objectId")
    int updateOnlineStatus(@Param("objectId") int objectId, @Param("online") boolean online, @Param("lastAccess") long lastAccess);

    @Modifying
    @Query("UPDATE characters SET in_seven_signs=:inDungeon WHERE object_id=:objectId")
    int updateSevenSignsDungeonStatus(@Param("objectId") int objectId, @Param("inDungeon") int isInDungeon);

    @Modifying
    @Query("UPDATE characters SET power_grade=:powerGrade WHERE object_id=:objectId")
    int updatePowerGrade(@Param("objectId") int objectId, @Param("powerGrade") int powerGrade);

    @Query("SELECT clan FROM characters WHERE name=:name")
    int findClanIdByName(@Param("name") String name);

    @Query("SELECT object_id FROM characters WHERE name=:name")
    int findIdByName(@Param("name") String name);

    @Modifying
    @Query("UPDATE characters SET online=:online")
    int updateAllOnlineStatus(@Param("online") boolean online);

    @Modifying
    @Query("UPDATE characters SET apprentice=:apprentice,sponsor=:sponsor WHERE object_id=:objectId")
    int updateApprenticeAndSponsor(@Param("objectId") int objectId, @Param("apprentice") int apprentice, @Param("sponsor") int sponsor);

    @Query("SELECT Max(slot) FROM characters WHERE account=:account")
    int getLastCharSlotFromAccount(@Param("account") String account);
}
