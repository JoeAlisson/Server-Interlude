package org.l2j.gameserver.model.entity.database.repository;

import org.l2j.gameserver.model.entity.database.GlobalTasks;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GlobalTaskRepository extends CrudRepository<GlobalTasks, Integer> {

    @Modifying
    @Query("UPDATE global_tasks SET last_activation=:activation WHERE id=:id")
    int updateLastActivationById(@Param("id") int id, @Param("activation") long lastActivation);

    @Query("SELECT EXISTS (SELECT 1 FROM global_tasks WHERE task=:task)")
    boolean existsByTask(@Param("task") String task);
}
