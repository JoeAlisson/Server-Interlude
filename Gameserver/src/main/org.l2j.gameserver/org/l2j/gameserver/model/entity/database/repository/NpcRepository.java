package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.NpcTemplate;
import org.springframework.data.repository.CrudRepository;

public interface NpcRepository extends CrudRepository<NpcTemplate, Integer> {
}
