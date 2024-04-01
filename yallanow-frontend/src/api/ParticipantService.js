// Retrieves all RSVP'd users for a specific event, handling authentication and response
import axios from "axios";
import config from "../config/config";
import handleResponse from "./ResponseHelper";
import { getAuth,getIdToken  } from "firebase/auth";

class ParticipantService {

    constructor() {
        this.baseUrl = config.eventsBaseUrl;
        this.auth = getAuth();
    }

    // Fetches an ID token for the current user, throws error if not authenticated
    async fetchIdToken() {
        const user = this.auth.currentUser;
        if (!user) throw new Error("No authenticated user found");
        return getIdToken(user);
    }

    // RSVPs a user to an event, handling user data and authentication
    async addEventParticipant(userId, eventId, status) {

            const idToken = await this.fetchIdToken();
            const request = {
                userId: userId,
                eventId: eventId,
                participantStatus: status,
            };
            const response = await axios.post(`${this.baseUrl}/${eventId}/participants`, request, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": idToken
                },
            });
            return handleResponse(response);

    }

    async getEventParticipants(eventId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/events/${eventId}/participants`, {
                headers: {
                    "Authorization": idToken
                },
            });
            return handleResponse(response);

    }

    // Checks if a user has RSVP'd to an event, handling authentication and response
    async getEventParticipantStatus(userId, eventId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/${eventId}/participants/status`, {
                headers: {
                    "Authorization": idToken
                },
            });
            return handleResponse(response);

    }

    //  Updates the RSVP status of a user for an event, handling user data and authentication
    async updateEventParticipant(userId, eventId, status) {

            const idToken = await this.fetchIdToken();
            const request = {
                userId: userId,
                eventId: eventId,
                participantStatus: status,
            };
            const response = await axios.put(`${this.baseUrl}/${eventId}/participants`, request, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": idToken
                },
            });
            return handleResponse(response);

    }
    // UnRSVPs a user from an event, handling authentication and specific user data
    async deleteEventParticipant(userId, eventId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.delete(`${this.baseUrl}/${eventId}/participants`, {
                headers: {
                    "Authorization": idToken
                },
            });
            return handleResponse(response);

    }

}

const participantService = new ParticipantService();
export default participantService;