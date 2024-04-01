package org.ucalgary.events_service.DTO;

import java.time.LocalDateTime;

public class EventDTO {
    // Attributes
    private int eventId;
    private int groupId;
    private String eventTitle;
    private String eventDescription;
    private AddressDTO address;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private EventStatus status;
    private int count;
    private int capacity;
    private String imageUrl;
    
    // Constructors
    public EventDTO(){}

    public EventDTO(int eventId, int groupId,
                    String eventTitle, String eventDescription,
                    AddressDTO address, LocalDateTime eventStartTime, LocalDateTime eventEndTime,
                    EventStatus status, int count, int capacity, String imageUrl)throws IllegalArgumentException {
        checkEvent(eventId, groupId, eventTitle, eventDescription, address, eventStartTime, eventEndTime, status, count, capacity, imageUrl);
        this.eventId = eventId;
        this.groupId = groupId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.address = address;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.status = status;
        this.count = count;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
    }

    public EventDTO(int groupId, String eventTitle,
                    String eventDescription, AddressDTO address,
                    LocalDateTime eventStartTime,
                    LocalDateTime eventEndTime, EventStatus status,
                    int capacity, String imageUrl) throws IllegalArgumentException {
        checkEvent(groupId, eventTitle, eventDescription, address, eventStartTime, eventEndTime, status, capacity);
        this.groupId = groupId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.address = address;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.status = status;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
    }

    public EventDTO(int groupId, String eventTitle,
                    String eventDescription, AddressDTO address,
                    LocalDateTime eventStartTime,
                    LocalDateTime eventEndTime, EventStatus status,
                    int capacity) throws IllegalArgumentException{
        checkEvent(groupId, eventTitle, eventDescription, address, eventStartTime, eventEndTime, status, capacity);
        this.groupId = groupId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.address = address;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.status = status;
        this.capacity = capacity;
    }

    // Getters
    public final int getEventId() {return eventId;}
    public final int getGroupId() {return groupId;}
    public final String getEventTitle() {return eventTitle;}
    public final String getEventDescription() {return eventDescription;}
    public final AddressDTO getAddress() { return address; }
    public final LocalDateTime getEventStartTime() {return eventStartTime;}
    public final LocalDateTime getEventEndTime() {return eventEndTime;}
    public final EventStatus getStatus() {return status;}
    public final int getCount() {return count;}
    public final int getCapacity() {return capacity;}
    public final int getAddressId() {return address.getAddressId();}
    public final String getImageUrl() {return imageUrl;}

    // Setters
    public void setEventId(final int eventId) {this.eventId = eventId;}
    public void setGroupId(final int groupId) {this.groupId = groupId;}
    public void setEventTitle(final String eventTitle) {this.eventTitle = eventTitle;}
    public void setEventDescription(final String eventDescription) {this.eventDescription = eventDescription;}
    public void setAddress(final AddressDTO address) {this.address = address;}
    public void setEventStartTime(final LocalDateTime eventStartTime) {this.eventStartTime = eventStartTime;}
    public void setEventEndTime(final LocalDateTime eventEndTime) {this.eventEndTime = eventEndTime;}
    public void setStatus(final EventStatus status) {this.status = status;}
    public void setCount(final int count) {this.count = count;}
    public void setCapacity(final int capacity) {this.capacity = capacity;}
    public void setImageUrl(final String imageUrl) {this.imageUrl = imageUrl;}   

    private void checkEvent(Integer groupID, String eventTitle, String eventDescription, AddressDTO location,
                    LocalDateTime eventStartTime, LocalDateTime eventEndTime, EventStatus status, 
                    int capacity) throws IllegalArgumentException {
        if(groupID == null){
            throw new IllegalArgumentException("Group ID cannot be null");
        }else if (eventTitle == null || eventTitle.isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty.");
        }else if (eventDescription == null || eventDescription.isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be empty.");
        }else if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }else if (eventStartTime == null) {
            throw new IllegalArgumentException("Event start time cannot be null.");
        }else if (eventEndTime == null) {
            throw new IllegalArgumentException("Event end time cannot be null.");
        }else if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }else if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }

    private void checkEvent(Integer eventID, Integer groupID, String eventTitle, String eventDescription,
                            AddressDTO location, LocalDateTime eventStartTime, LocalDateTime eventEndTime,
                            EventStatus status, int count, int capacity, String imageUrl) throws IllegalArgumentException {
        if(eventID == null){
            throw new IllegalArgumentException("Event ID cannot be null");
        }else if(groupID == null){
            throw new IllegalArgumentException("Group ID cannot be null");
        }else if (eventTitle == null || eventTitle.isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be empty.");
        }else if (eventDescription == null || eventDescription.isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be empty.");
        }else if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }else if (eventStartTime == null) {
            throw new IllegalArgumentException("Event start time cannot be null.");
        }else if (eventEndTime == null) {
            throw new IllegalArgumentException("Event end time cannot be null.");
        }else if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }else if (count < 0) {
            throw new IllegalArgumentException("Count must be greater than or equal to 0.");
        }else if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
    }
}