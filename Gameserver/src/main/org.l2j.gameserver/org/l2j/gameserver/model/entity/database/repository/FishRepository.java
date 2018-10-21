package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.Fish;
import org.springframework.data.repository.CrudRepository;

public interface FishRepository extends CrudRepository<Fish, Integer> {
}
