package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.PetsStats;
import org.springframework.data.repository.CrudRepository;

public interface PetStatsRepository extends CrudRepository<PetsStats, Integer> {
}
