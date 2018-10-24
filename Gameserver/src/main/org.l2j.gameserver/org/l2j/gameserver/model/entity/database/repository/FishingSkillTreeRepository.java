package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.FishingSkill;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FishingSkillTreeRepository extends CrudRepository<FishingSkill, Integer> {

    @Query("SELECT * FROM fishing_skill_trees WHERE isForDwarf=0 ORDER BY skill_id, level")
    List<FishingSkill> findForNonDwarf();

    @Query("SELECT * FROM fishing_skill_trees WHERE isForDwarf=1 ORDER BY skill_id, level")
    List<FishingSkill> findForDwarf();
}
