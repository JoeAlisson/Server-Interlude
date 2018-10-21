package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.PlayerTemplate;
import org.springframework.data.repository.CrudRepository;

public interface CharTemplateRepository extends CrudRepository<PlayerTemplate, Integer> {
}
