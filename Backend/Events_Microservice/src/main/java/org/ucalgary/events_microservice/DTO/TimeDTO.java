package org.ucalgary.events_microservice.DTO;

import java.time.LocalTime;

public class TimeDTO {
    // Attributes
    private int timeID;
    private int hour;
    private int minute;

    // Constructors
    public TimeDTO() {}
    
    public TimeDTO(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public TimeDTO(int timeID, int hour, int minute) {
        this.timeID = timeID;
        this.hour = hour;
        this.minute = minute;
    }

    // Getters and setters
    public final int getTimeID() {return timeID;}
    public final int getHour() {return hour;}
    public final int getMinute() {return minute;}

    public void setHour(final int hour) {this.hour = hour;}
    public void setMinute(final int minute) {this.minute = minute;}


    // Override Methods
    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }

    public boolean isAfter(TimeDTO otherTime) {
        LocalTime thisTime = LocalTime.of(this.hour, this.minute);
        LocalTime otherLocalTime = LocalTime.of(otherTime.getHour(), otherTime.getMinute());
        return thisTime.isAfter(otherLocalTime);
    }
}