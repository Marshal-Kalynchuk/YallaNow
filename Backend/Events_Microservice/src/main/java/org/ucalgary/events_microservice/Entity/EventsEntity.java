package org.ucalgary.events_microservice.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.ucalgary.events_microservice.DTO.EventStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * EventsEntity, Used to Create and Store EventsEntity Objects in MySQL Database
 */
@Entity
@Table(name = "event_table")
public class EventsEntity {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;
    @Column(name = "group_id")
    private Integer groupId;
    @Column(name = "event_title")
    private String eventTitle;
    @Column(name = "event_description")
    private String eventDescription;
    @Column(name = "location_id")
    private Integer locationId;
    @Column(name = "event_date")
    private LocalDate eventDate;
    @Column(name = "event_start_time")
    private LocalTime eventStartTime;
    @Column(name = "event_end_time")
    private LocalTime eventEndTime;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @Column(name = "count")
    private Integer count;
    @Column(name = "capacity")
    private Integer capacity;
    @Column(name = "image_id")
    private Integer imageId;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "address_id", insertable = false, updatable = false)
    private AddressEntity address;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantEntity> participants;
    
    // Constructors
    public EventsEntity() {
    }

    public EventsEntity(Integer eventId, Integer groupId, 
                        String eventTitle, String eventDescription, 
                        Integer locationId, LocalDate eventDate, 
                        LocalTime eventStartTime, LocalTime eventEndTime, 
                        EventStatus status, Integer count, Integer capacity, Integer imageId) {
        this.eventId = eventId;
        this.groupId = groupId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.locationId = locationId;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.status = status;
        this.count = count;
        this.capacity = capacity;
        this.imageId = imageId;
    }

    // Getters and setters
    public Integer getEventId() {return eventId;}
    public Integer getGroupId() {return groupId;}
    public String getEventTitle() {return eventTitle;}
    public String getEventDescription() {return eventDescription;}
    public Integer getLocationId() {return locationId;}
    public LocalDate getEventDate() {return eventDate;}
    public LocalTime getEventStartTime() {return eventStartTime;}
    public LocalTime getEventEndTime() {return eventEndTime;}
    public EventStatus getStatus() {return status;}
    public Integer getCount() {return count;}
    public Integer getCapacity() {return capacity;}
    public Integer getImageId() {return imageId;}

    public void setEventId(Integer eventId) {this.eventId = eventId;}
    public void setGroupId(Integer groupId) {this.groupId = groupId;}
    public void setEventTitle(String eventTitle) {this.eventTitle = eventTitle;}
    public void setEventDescription(String eventDescription) {this.eventDescription = eventDescription;}
    public void setLocationId(Integer locationId) {this.locationId = locationId;}
    public void setEventDate(LocalDate eventDate) {this.eventDate = eventDate;}
    public void setEventStartTime(LocalTime eventStartTime) {this.eventStartTime = eventStartTime;}
    public void setEventEndTime(LocalTime eventEndTime) {this.eventEndTime = eventEndTime;}
    public void setStatus(EventStatus status) {this.status = status;}
    public void setCount(Integer count) {this.count = count;}
    public void setCapacity(Integer capacity) {this.capacity = capacity;}
    public void setImageId(Integer imageId) {this.imageId = imageId;}
}

