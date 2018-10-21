package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.WalkerRouteNode;
import org.springframework.data.repository.CrudRepository;

public interface WalkerRoutesRepository extends CrudRepository<WalkerRouteNode, Integer> {
}
