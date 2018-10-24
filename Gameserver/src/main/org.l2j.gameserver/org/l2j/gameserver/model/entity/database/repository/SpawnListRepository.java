package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.Spawn;
import org.springframework.data.repository.CrudRepository;

public interface SpawnListRepository extends CrudRepository<Spawn, Integer> {
}
