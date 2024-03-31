package org.ucalgary.events_service.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.ucalgary.events_service.DTO.ParticipantDTO;
import org.ucalgary.events_service.DTO.ParticipantStatus;
import org.ucalgary.events_service.Entity.EventsEntity;
import org.ucalgary.events_service.Entity.ParticipantEntity;
import org.ucalgary.events_service.Service.EventService;
import org.ucalgary.events_service.Service.ParticipantService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/events/{eventId}/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final EventService eventService;

    public ParticipantController(ParticipantService ParticipantService, EventService eventService) {
        this.participantService = ParticipantService;
        this.eventService = eventService;
    }

    /**
     * Add a participant to an event
     * @param participant
     * @return Response Entity with the object of the participant added
     */
    @PostMapping
    public ResponseEntity<?> addParticipant(@PathVariable int eventId, @RequestBody ParticipantDTO participant, @RequestAttribute("Id") String userId,
                                            @RequestAttribute("Email") String email, @RequestAttribute("Name") String name) {
        try{
            validateParticipant(eventId, participant, userId);
            participant.setUserId(userId);
            EventsEntity event = eventService.getEvent(eventId);
            ParticipantEntity participants = participantService.addParticipantToEvent(participant, email, name, event);
            return ResponseEntity.status(HttpStatus.CREATED).body(participants);
        }catch(IllegalArgumentException e){
            return (ResponseEntity<?>) ResponseEntity.status(422);
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all participants for an event
     * @param event
     * @return Response Entity with the list of participants for the event
     */

    @GetMapping
    public ResponseEntity<?> getAllParticipantsForEvent(@PathVariable int eventId) {
        List<Map<String, String>> participants = participantService.getAllParticipantsForEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    /**
     * Get the status of a participant for an event
     * @param eventId
     * @param userId
     * @return Response Entity with the status of the participant for the event
     */
    @GetMapping("/status")
    public ResponseEntity<String> getParticipantStatus(@PathVariable int eventId, @RequestAttribute("Id") String userId) {
        Optional<ParticipantStatus> status = participantService.getParticipantStatus(userId, eventId);
        return ResponseEntity.ok(status.map(ParticipantStatus::toString).orElse(ParticipantStatus.NotAttending.toString()));
    }

    /**
     * Update a participant for an event
     * @param participant
     * @return Response Entity with the object of the participant updated
     */
    @PutMapping
    public ResponseEntity<?> updateParticipant(@PathVariable int eventId, @RequestBody ParticipantDTO participant, @RequestAttribute("Id") String userId,
                                               @RequestAttribute("Email") String email, @RequestAttribute("Name") String name) {
        try{
            validateParticipant(eventId, participant, userId);
            participant.setUserId(userId);
            EventsEntity event = eventService.getEvent(eventId);
            ParticipantEntity participants = participantService.updateParticipant(participant, email, name, event);
            return ResponseEntity.status(HttpStatus.OK).body(participants);
        }catch(IllegalArgumentException e){
            return (ResponseEntity<?>) ResponseEntity.status(422);
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void validateParticipant(@PathVariable int eventId, @RequestBody ParticipantDTO participant, @RequestAttribute("Id") String userId) {
        if (participant.getEventId() != eventId) {
            throw new IllegalArgumentException("Mismatch in event ID");
        }
        if (!participant.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Mismatch in user ID");
        }
    }

    /**
     * Delete a participant from an event
     * @param eventId
     * @param userId
     * @return Response Entity with the object of the participant deleted
     */
    @DeleteMapping
    public ResponseEntity<?> deleteParticipant(@PathVariable int eventId, @RequestAttribute("Id") String userId) {
        try {
            participantService.deleteParticipant(userId, eventId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
