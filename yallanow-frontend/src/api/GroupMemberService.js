import axios from 'axios';
import config from '../config/config';
import handleResponse from "./ResponseHelper";
import { getAuth,getIdToken  } from "firebase/auth";

class GroupMemberService {
    constructor() {
        this.auth = getAuth();
        this.baseUrl = config.groupsBaseUrl;
    }

    // Fetches ID token for the current user, throws an error if user not authenticated
    async fetchIdToken() {
        const user = this.auth.currentUser;
        if (!user) throw new Error("No authenticated user found");
        return getIdToken(user);
    }

    // Retrieves members of a specific group, handling authentication and response
    async getGroupMembers(groupID) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/${groupID}/members`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Adds a new member to a specific group, handling authentication and response
    async addGroupMember(groupID, memberData) {

            const idToken = await this.fetchIdToken();
            const response = await axios.post(`${this.baseUrl}/${groupID}/members`, memberData, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Removes a member from a specific group, handling authentication and logging
    async removeGroupMember(groupID, userID) {

            console.log("before syc")
            const idToken = await this.fetchIdToken();
            console.log(groupID)
            const response = await axios.delete(`${this.baseUrl}/${groupID}/members/${userID}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            
            console.log("success")
            return handleResponse(response, true);
            

    }

    // Retrieves information of a specific group member, handling authentication and response
    async getGroupMember(groupID, userID) {

            const idToken = await this.fetchIdToken();
            const response = await axios.get(`${this.baseUrl}/${groupID}/members/${userID}`, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

    // Updates information for a member of a specific group, handling authentication and response
    async updateGroupMember(groupID, userID, memberData) {

            const idToken = await this.fetchIdToken();
            const response = await axios.put(`${this.baseUrl}/${groupID}/members/${userID}`, memberData, {
                headers: { 
                    "Authorization": idToken
            },    
            });
            return handleResponse(response);

    }

}

const groupMemberService = new GroupMemberService();
export default groupMemberService;
