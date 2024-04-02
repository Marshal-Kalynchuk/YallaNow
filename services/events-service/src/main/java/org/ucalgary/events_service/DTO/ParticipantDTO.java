package org.ucalgary.events_service.DTO;

public class ParticipantDTO {
    // Attributes
    private int participantId;
    private String userId;
    private int eventId;
    private ParticipantStatus ParticipantStatus;

    // Constructors
    public ParticipantDTO() {}

    public ParticipantDTO(String userId, int event_id , ParticipantStatus ParticipantStatus) {
        this.userId = userId;
        this.eventId = event_id;
        this.ParticipantStatus = ParticipantStatus;
    }

    public ParticipantDTO(int participantId, String userId, int eventId, ParticipantStatus ParticipantStatus) {
        this.participantId = participantId;
        this.userId = userId;
        this.eventId = eventId;
        this.ParticipantStatus = ParticipantStatus;
    }

    // Getters and setters
    public final  int getParticipantId() {return participantId;}
    public final  String getUserId() {return userId;}
    public final  int getEventId() {return eventId;}
    public final ParticipantStatus getParticipantStatus() {return ParticipantStatus;}

    public void setParticipantId(final int participantId) {this.participantId = participantId;}
    public void setUserId(final String userId) {this.userId = userId;}
    public void setEventId(final int eventId) {this.eventId = eventId;}
    public void setParticipantStatus(final ParticipantStatus ParticipantStatus) {this.ParticipantStatus = ParticipantStatus;}

}
