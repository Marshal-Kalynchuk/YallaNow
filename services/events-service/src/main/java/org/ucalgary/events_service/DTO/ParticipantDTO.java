package org.ucalgary.events_service.DTO;

public class ParticipantDTO {
    // Attributes
    private int participantID;
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

    public ParticipantDTO(int participantID, String userId, int eventId, ParticipantStatus ParticipantStatus) {
        this.participantID = participantID;
        this.userId = userId;
        this.eventId = eventId;
        this.ParticipantStatus = ParticipantStatus;
    }

    // Getters and setters
    public final  int getParticipantID() {return participantID;}
    public final  String getUserId() {return userId;}
    public final  int getEventId() {return eventId;}
    public final ParticipantStatus getParticipantStatus() {return ParticipantStatus;}

    public void setParticipantID(final int participantID) {this.participantID = participantID;}
    public void setUserId(final String userId) {this.userId = userId;}
    public void setEventId(final int eventId) {this.eventId = eventId;}
    public void setParticipantStatus(final ParticipantStatus ParticipantStatus) {this.ParticipantStatus = ParticipantStatus;}

}
