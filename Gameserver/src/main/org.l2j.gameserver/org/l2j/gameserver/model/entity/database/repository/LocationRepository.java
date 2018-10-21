package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.Locations;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Locations, Integer> {
}
