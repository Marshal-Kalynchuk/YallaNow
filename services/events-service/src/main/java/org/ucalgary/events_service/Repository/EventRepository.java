package org.ucalgary.events_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ucalgary.events_service.Entity.EventsEntity;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repository interface for managing EventsEntity objects in the database.
 * This interface provides methods to perform CRUD operations on EventsEntity objects.
 */
@Repository
public interface EventRepository extends JpaRepository<EventsEntity, Integer>{
    Optional<EventsEntity> findEventByEventId(Integer eventId);
    Optional<ArrayList<EventsEntity>> findEventsByGroupId(Integer groupId);
}
