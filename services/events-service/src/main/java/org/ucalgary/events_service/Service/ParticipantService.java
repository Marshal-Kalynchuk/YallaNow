package org.ucalgary.events_service.Service;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.ucalgary.events_service.Repository.ParticipantRepository;
import org.ucalgary.events_service.Repository.EventRepository;
import org.ucalgary.events_service.Entity.ParticipantEntity;
import org.ucalgary.events_service.DTO.ParticipantStatus;
import org.ucalgary.events_service.Entity.EventsEntity;
import org.ucalgary.events_service.DTO.ParticipantDTO;
import org.ucalgary.events_service.DTO.EventStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Service class for managing ParticipantEntity objects.
 * This class provides methods to add, update, retrieve, and delete participants.
 */
@Service
public class ParticipantService {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final EventsPubService eventsPubService;
    private final MailSenderService mailSenderService;

    public ParticipantService(EventRepository eventRepository,ParticipantRepository participantRepository, 
                                EventsPubService eventsPubService, MailSenderService mailSenderService) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.eventsPubService = eventsPubService;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Adds a participant to an event.
     * @param user The participant DTO containing user and event information.
     * @return The participant entity added to the event.
     * @throws IllegalArgumentException if the participant already exists.
     */
    @Transactional
    public ParticipantEntity addParticipantToEvent(ParticipantDTO user, String email, String name, EventsEntity event) throws IllegalArgumentException, EntityNotFoundException {
        Optional<ParticipantEntity> checkDup = participantRepository.findByUserIdAndEvent_EventId(user.getUserId(), user.getEventId());
        if (checkDup.isPresent()) {
            logger.info("Participant {} already exists for event {}. Updating participant.", user.getUserId(), user.getEventId());
            return updateParticipant(user, email, name, event);
        }

        ParticipantEntity participant = new ParticipantEntity(user.getUserId(), user.getParticipantStatus(), event);
        updateEventCount(event, user.getParticipantStatus(), email, name);
        ParticipantEntity savedParticipant = participantRepository.save(participant);
        logger.info("Added participant {} to event {}", savedParticipant.getUserId(), savedParticipant.getEvent().getEventId());
        return savedParticipant;
    }


    /**
     * Retrieves the status of a participant for an event.
     * @param userId The ID of the user to retrieve the status for.
     * @param eventId The ID of the event to retrieve the status for.
     * @return The status of the participant for the event.
     * @throws EntityNotFoundException if the participant with the given user ID and event ID does not exist.
     */
    @Transactional
    public Optional<ParticipantStatus> getParticipantStatus(String userId, int eventId) {
        return participantRepository.findByUserIdAndEvent_EventId(userId, eventId)
                .map(ParticipantEntity::getParticipantStatus);
    }


// Add similar logging statements to other methods as needed


    /**
     * Retrieves all events for a participant.
     * @param userId The ID of the user to retrieve events for.
     * @return A list of event and status data for the participant.
     */
    @Transactional
    public List<Map<String, Object>> getEventsForParticipant(String userId) {
        List<ParticipantEntity> participantEntities = participantRepository.findAllByUserId(userId);
        if (participantEntities.isEmpty()) {
            logger.info("No events registered for user {}", userId);
            return Collections.emptyList();
        }

        logger.info("Retrieved events for participant {}", userId);
        return participantEntities.stream().map(participant -> {
            EventsEntity event = participant.getEvent();
            ParticipantStatus status = participant.getParticipantStatus();
            return Map.of("event", event, "status", status);
        }).collect(Collectors.toList());
    }


    /**
     * Retrieves all participants for an event.
     * @param eventId The ID of the event to retrieve participants for.
     * @return A list of participants associated with the event.
     */
    @Transactional
    public ArrayList<ParticipantEntity> getAllEventParticipants(int eventId) {
        Optional<ArrayList<ParticipantEntity>> optionalParticipants = participantRepository.findAllByEvent_EventId(eventId);
        ArrayList<ParticipantEntity> participants = optionalParticipants.orElseGet(ArrayList::new);
        logger.info("Retrieved {} participants for event {}", participants.size(), eventId);
        return participants;
    }

    /**
     * Retrieves all participants for an event.
     * @param eventId The event DTO to retrieve participants for.
     * @return A list of participants associated with the event.
     */
    @Transactional
    public List<Map<String, String>> getAllParticipantsForEvent(int eventId) {
        ArrayList<ParticipantEntity> participants = getAllEventParticipants(eventId);
        logger.info("Retrieved {} participants for event {}", participants.size(), eventId);
        return participants.stream().map(participant -> Map.of(participant.getUserId(), participant.getParticipantStatus().toString())).collect(Collectors.toList());
    }

    /**
     * Updates the status of a participant for an event.
     * @param oldParticipant The participant DTO containing the updated status.
     * @return The updated participant entity.
     * @throws IllegalArgumentException if the participant does not exist.
     */
    @Transactional
    public ParticipantEntity updateParticipant(ParticipantDTO oldParticipant, String email, String name, EventsEntity event) throws IllegalArgumentException {
        Optional<ParticipantEntity> optionalParticipant = participantRepository.findByUserIdAndEvent_EventId(oldParticipant.getUserId(), oldParticipant.getEventId());
        return optionalParticipant.map(participant -> {
            ParticipantStatus newStatus = oldParticipant.getParticipantStatus();
            updateEventCount(event, participant.getParticipantStatus(), newStatus, email, name);
            participant.setParticipantStatus(newStatus);
            logger.info("Updated status of participant {} for event {} to {}", participant.getUserId(), participant.getEvent().getEventId(), newStatus);
            return participantRepository.save(participant);
        }).orElseGet(() -> addParticipantToEvent(oldParticipant, email, name, event));
    }


    /**
     * Deletes a participant from an event.
     * @param userID The ID of the user to delete the participant for.
     * @param eventID The ID of the event to delete the participant from.
     * @throws EntityNotFoundException if the participant with the given user ID and event ID does not exist.
     */
    @Transactional
    public void deleteParticipant(String userID, int eventID) throws EntityNotFoundException {
        Optional<ParticipantEntity> participant = participantRepository.findByUserIdAndEvent_EventId(userID, eventID);
        participant.ifPresentOrElse(
                p -> {
                    participantRepository.deleteById(p.getParticipantId());
                    logger.info("Deleted participant {} from event {}", userID, eventID);
                },
                () -> {
                    logger.error("Event participant not found for user {} and event {}", userID, eventID);
                    throw new EntityNotFoundException("Event Particiapant not found");
                }
        );
    }

    /**
     * Updates the count of participants for an event based on status changes.
     * @param event The event for which to update the count.
     * @param newStatus The new status of the participant.
     * @throws IllegalStateException if the event has reached its capacity.
     */
    private void updateEventCount(EventsEntity event, ParticipantStatus newStatus, String email, String name) throws IllegalArgumentException {
        if (newStatus != ParticipantStatus.NotAttending) {
            event.setCount(event.getCount() + 1);
            if (event.getCapacity() != null && event.getCount() > event.getCapacity()) {
                throw new IllegalStateException("Event has reached its capacity. Cannot add more participants.");
            }
            if (event.getStatus() == EventStatus.Cancelled) {
                throw new IllegalStateException("Event is cancelled. Cannot add participant.");
            }
            eventRepository.save(event);
            mailSenderService.sendMessage(event, name, email, newStatus);
            if (event.getCapacity().equals(event.getCount())) {
                eventsPubService.publishEvents(event, "UPDATE");
            }
        }
    }

    private void updateEventCount(EventsEntity event, ParticipantStatus oldStatus, ParticipantStatus newStatus, String email, String name) throws IllegalArgumentException {
        if (newStatus != oldStatus) {
            if (event.getStatus() == EventStatus.Cancelled) {
                throw new IllegalStateException("Event is cancelled. Cannot add participant.");
            }

            if (newStatus == ParticipantStatus.NotAttending && (oldStatus == ParticipantStatus.Attending || oldStatus == ParticipantStatus.Maybe)) {
                if (event.getCapacity().equals(event.getCount())) {
                    event.setCount(event.getCount() - 1);
                    eventsPubService.publishEvents(event, "UPDATE");
                } else {
                    event.setCount(event.getCount() - 1);
                }
            } else if (oldStatus == ParticipantStatus.NotAttending && (newStatus == ParticipantStatus.Attending || newStatus == ParticipantStatus.Maybe)) {
                event.setCount(event.getCount() + 1);
                if (event.getCapacity() != null && event.getCount() > event.getCapacity()) {
                    throw new IllegalStateException("Event has reached its capacity. Cannot add more participants.");
                }
            }
            eventRepository.save(event);
            mailSenderService.sendMessage(event, name, email, newStatus);
            if (event.getCapacity().equals(event.getCount())) {
                eventsPubService.publishEvents(event, "UPDATE");
            }
        }
    }
}

