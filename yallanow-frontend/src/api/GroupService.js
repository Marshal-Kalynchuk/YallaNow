import axios from "axios";
import config from "../config/config";
import handleResponse from "./ResponseHelper";
import { getAuth, getIdToken  } from "firebase/auth";

class GroupService {
    constructor() {
        this.auth = getAuth();
        this.baseUrl = config.groupsBaseUrl;
    }

    // Fetches Firebase ID token for the current user, required for authentication
    async fetchIdToken() {
        const user = this.auth.currentUser;
        if (!user) throw new Error("No authenticated user found");
        return getIdToken(user);
    }
    
    // Creates a new group with the provided group data, handling authentication
    async createGroup(groupData) {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.post(this.baseUrl, groupData, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);
        } catch (error) {
            if (error.response.status === 409) {
                throw Error("Group with that name already exists!")
            }
            handleResponse(error.response);
        }
    }

    // Retrieves a list of all groups, handling authentication
    async getGroups() {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.get(this.baseUrl, {
                headers: { 
                    "Authorization": idToken
                },
            });
            return handleResponse(response);
        } catch (error) {
            handleResponse(error.response);
        }
    }

    // Retrieves details for a specific group by ID, handling authentication
    async getGroup(groupID) {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/${groupID}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);
        } catch (error) {
            handleResponse(error.response);
        }
    }

    // Updates details for a specific group by ID, handling authentication
    async updateGroup(groupID, groupData) {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.put(`${this.baseUrl}/${groupID}`, groupData, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);
        } catch (error) {
            handleResponse(error.response);
        }
    }

    // Deletes a specific group by ID, handling authentication
    async deleteGroup(groupID) {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.delete(`${this.baseUrl}/${groupID}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);
        } catch (error) {
            handleResponse(error.response);
        }
    }

    // Retrieves groups associated with a specific user ID, handling authentication
    async getGroupByUserID(userID) {
        try {
            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/user/${userID}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);
        } catch (error) {
            handleResponse(error.response);
        }
    }


}

const groupService = new GroupService()
export default groupService;
