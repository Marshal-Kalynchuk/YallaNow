package com.yallanow.analyticsservice.utils;

import com.yallanow.analyticsservice.models.Item;
import com.yallanow.analyticsservice.models.Location;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The ItemConverter class is responsible for converting Item objects to a map representation
 * and vice versa. It provides methods to convert an Item object to a map and extract an Item
 * object from a map.
 */
@Component
public class ItemConverter {

/**
 * Converts an Item object to a Map object in order to be used for Recombee API.
 * 
 * @param item the Item object to be converted
 * @return a Map object containing the converted item properties
 */
    public Map<String, Object> convertItemToRecombeeMap(Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", item.getGroupId());
        map.put("eventTitle", item.getTitle());
        map.put("eventDescription", item.getDescription());

        map.put("eventStartTime", item.getStartTime().toString());
        map.put("eventEndTime", item.getEndTime().toString());

        map.put("eventLocationStreet", item.getLocation().getStreet());
        map.put("eventLocationCity", item.getLocation().getCity());
        map.put("eventLocationProvince", item.getLocation().getProvince());
        map.put("eventLocationCountry", item.getLocation().getCountry());

        map.put("eventAttendeeCount", item.getAttendeeCount());
        map.put("eventCapacity", item.getCapacity());
        map.put("eventStatus", item.getStatus());
        map.put("eventImageUrl", item.getImageUrl());
        return map;
    }

/**
 * Retrieves the ID from the given PubSub message map.
 *
 * @param map the map containing the PubSub message data
 * @return the ID extracted from the map
 * @throws IllegalArgumentException if the 'eventId' key is missing in the map
 */
    public String getIdFromPubSubMessage(Map<String, Object> map) {
        return String.valueOf(Optional.ofNullable(map.get("eventId"))
                .orElseThrow(() -> new IllegalArgumentException("Missing 'eventId' in event data")));
    }

/**
 * Represents an item with event details. 
 * Collect the item details from the PubSub message map.
 */
    public Item getItemFromPubsubMessage(Map<String, Object> map) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        try {
            String eventId = String.valueOf(Optional.ofNullable(map.get("eventId"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'eventId' in event data")));

            String groupId = String.valueOf(Optional.ofNullable(map.get("groupId"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'groupId' in event data")));

            String eventTitle = Optional.ofNullable((String) map.get("eventTitle"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'eventTitle' in event data"));

            String eventDescription = Optional.ofNullable((String) map.get("eventDescription"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'eventDescription' in event data"));

            LocalDateTime startTime = parseDateTime(map.get("eventStartTime"), formatter);
            LocalDateTime endTime = parseDateTime(map.get("eventEndTime"), formatter);

            String eventStreet = String.valueOf(Optional.ofNullable(map.get("street"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'street' in event data")));

            String eventCity = Optional.ofNullable((String) map.get("city"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'city' in event data"));

            String eventProvince = Optional.ofNullable((String) map.get("province"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'province' in event data"));

            String eventCountry = Optional.ofNullable((String) map.get("country"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'country' in event data"));

            Location location = new Location(
                    eventStreet,
                    eventCity,
                    eventProvince,
                    eventCountry
            );

            Integer eventAttendeeCount = Optional.ofNullable((Integer) map.get("count"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'count' in event data"));

            Integer eventCapacity = Optional.ofNullable((Integer) map.get("capacity"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'capacity' in event data"));

            String eventStatus = Optional.ofNullable((String) map.get("status"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing 'status' in event data"))
                    .toLowerCase(); // Convert the status to lowercase

            String eventImageUrl = (String) map.get("imageUrl");

            return new Item(
                    eventId,
                    groupId,
                    eventTitle,
                    eventDescription,
                    startTime,
                    endTime,
                    location,
                    eventAttendeeCount,
                    eventCapacity,
                    eventStatus,
                    eventImageUrl
            );
        } catch (Exception e) {
            throw new IOException("Error constructing item from map: " + e.getMessage(), e);
        }

    }
    private LocalDateTime parseDateTime(Object dateTimeObj, DateTimeFormatter formatter) {
        if (dateTimeObj instanceof String) {
            return LocalDateTime.parse((String) dateTimeObj, formatter);
        } else if (dateTimeObj instanceof List) {
            List<Integer> dateTimeList = (List<Integer>) dateTimeObj;
            return LocalDateTime.of(
                    dateTimeList.get(0), // year
                    dateTimeList.get(1), // month
                    dateTimeList.get(2), // day
                    dateTimeList.get(3), // hour
                    dateTimeList.get(4)  // minute
            );
        } else {
            throw new IllegalArgumentException("Invalid date time format: " + dateTimeObj);
        }
    }
}
