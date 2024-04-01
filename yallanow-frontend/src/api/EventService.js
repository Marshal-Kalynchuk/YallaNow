import EventServiceApi from "./EventServiceApi";

class EventService {

    // Retrieves a single event by its ID and formats the received data.
    async getEvent(eventId) {
        const rawEvent = await EventServiceApi.getEvent(eventId);
        return this.formatEventFromEventService(rawEvent);
    }

    // Creates a new event with predefined image URL, formats data for sending and receiving.
    async createEvent(eventData) {
        const requestData = this.formatEventForEventService(eventData);
        const rawEvent = await EventServiceApi.createEvent(requestData);
        return this.formatEventFromEventService(rawEvent);
    }

   // Updates an existing event by formatting and sending data, then formatting the response.
    async updateEvent(eventData) {
        const requestData = this.formatEventForEventService(eventData);
        const rawEvent = await EventServiceApi.updateEvent(requestData);
        return this.formatEventFromEventService(rawEvent);
    }

    // Deletes an event by its ID.
    async deleteEvent(eventId) {
        return await EventServiceApi.deleteEvent(eventId);
    }

    // Fetches events for a specific group and formats each received event.
    async getEventsForGroup(groupId) {
        const rawEvents = await EventServiceApi.getEventsForGroup(groupId);
        console.log(rawEvents); // Check what rawEvents contains

        return await Promise.all(rawEvents.map(async (event) => {
            return this.formatEventFromEventService(event);
        }));
    }

    // Fetches events a user has RSVP'd to and formats each received event.
    async getEventsForParticipant(userId) {
        const rawEvents = await EventServiceApi.getEventsForParticipant(userId);
        return rawEvents.map(async (event) => this.formatEventFromEventService(event));
    }

    // Formats event data before sending it to the event service.
    formatEventForEventService(data) {

        return {

            eventId: data.eventId,
            groupId: data.groupId,
            eventTitle: data.eventTitle,
            eventDescription: data.eventDescription,
            location: {
                street: data.eventLocationStreet,
                city: data.eventLocationCity,
                province: data.eventLocationProvince,
                country: data.eventLocationCountry,
                postalCode: data.eventLocationPostalCode,
            },
            eventStartTime: data.eventStartTime,
            eventEndTime: data.eventEndTime,
            status: data.eventStatus,
            capacity: 1,
            count: 1,

            imageUrl: data.imageUrl,
        };
    }

    // Formats the event data received from the event service.
    formatEventFromEventService(data, imageUrl) {
        const formatDateTime = (dateTimeArray) => {
            // Assuming the array format is [year, month, day, hour, minute]
            const [year, month, day, hour, minute] = dateTimeArray;
            // Construct a Date object and format it to a readable string
            return new Date(year, month - 1, day, hour, minute);

        };

        return {
            eventId: data.eventId,
            groupId: data.groupId,
            eventTitle: data.eventTitle,
            eventDescription: data.eventDescription,

            eventLocationStreet: data.address.street,
            eventLocationCity: data.address.city,
            eventLocationProvince: data.address.province,
            eventLocationCountry: data.address.country,
            eventLocationPostalCode: data.address.postalCode,

            eventStartTime: formatDateTime(data.eventStartTime),
            eventEndTime: formatDateTime(data.eventEndTime),
            eventStatus: data.status,
            eventCapacity: data.capacity,
            eventAttendeeCount: data.count,
            eventImageUrl: imageUrl,
        };
    }

}

const eventService = new EventService()
export default eventService;