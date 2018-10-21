package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.ZoneVertices;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZoneVerticesRepository extends CrudRepository<ZoneVertices, Integer> {

    @Query("SELECT * FROM zone_vertices WHERE id=:zone ORDER BY `order` ASC ")
    List<ZoneVertices> findAllOrderedById(@Param("zone") int zoneId);
}