package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.CursedWeapons;
import org.springframework.data.repository.CrudRepository;

public interface CursedWeaponRepository extends CrudRepository<CursedWeapons, Integer> {
}
