import axios from "axios";
import config from "../config/config";
import handleResponse from "./ResponseHelper"
import { getAuth,getIdToken  } from "firebase/auth";

class EventServiceApi {
    // Base URL for API calls, loaded from config and Initialize Firebase Auth
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

    // Creates an event with provided data, handling authentication and response
    async createEvent(eventRequest) {

            const idToken = await this.fetchIdToken();
            const response = await axios.post(this.baseUrl, eventRequest, {
                headers: { 
                    "Content-Type": "application/json",
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Updates an existing event with new data, handling authentication and response
    async updateEvent(eventRequest) {

            const idToken = await this.fetchIdToken();
            const response = await axios.put(this.baseUrl, eventRequest, {
                    headers: { 
                        "Content-Type": "application/json",
                        "Authorization": idToken
                },    
                });
            return handleResponse(response);

    }

    // Retrieves a single event by ID, handling authentication and response
    async getEvent(eventId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/${eventId}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Fetches all events for a specific group, handling authentication and response
    async getEventsForGroup(groupId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/group/${groupId}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Deletes an event by ID, handling authentication and response
    async deleteEvent(eventId) {

            const idToken = await this.fetchIdToken();
            const response = await axios.delete(`${this.baseUrl}/${eventId}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Fetches all events a specific user has RSVP'd to, handling authentication and response
    async getEventsForParticipant(userId) {

        const idToken = await this.fetchIdToken();
        const response = await axios.get(`${this.baseUrl}/participants/${userId}`, {
            headers: {
                "Authorization": idToken
            },
        });
        return handleResponse(response);

    }
}

const eventServiceApi =  new EventServiceApi()
export default eventServiceApi;
