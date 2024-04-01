import axios from "axios";
import config from "../config/config";
import handleResponse from "./ResponseHelper"

class FeedService {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Transforms recommendation data into event format, adjusting time values
    formatRecommendationsToEvents(recommendations) {
        return recommendations.map((recommendation) => {
            return {
                ...recommendation.properties,
                eventId: recommendation.id,
                eventStartTime: new Date(recommendation.properties.eventStartTime * 1000),
                eventEndTime: new Date(recommendation.properties.eventEndTime * 1000)
            };
        });
    }

    // Fetches recommendations from the backend, handling responses or errors
    async getRecommendations(recommendationRequest) {

            const response = await axios.post(this.baseUrl, recommendationRequest, {
                headers: {
                    'Content-Type': 'application/json',
                    'mode': 'cors'
                },
            });
            handleResponse(response);
            return {
                recommId: response.data.recommId,
                recommendations: this.formatRecommendationsToEvents(response.data.recommendations)
            }

    }

    // Fetches default event recommendations
    async getDefaultEvents(userId, count) {
        const request = {
            userId: userId,
            count: count,
            scenario: 'default-items',
            recommendationType: 'ItemsToUser'
        };
        return await this.getRecommendations(request);
    }

    // Fetches homepage event recommendations
    async getHomepageEvents(userId, count) {
        const request = {
            userId: userId,
            count: count,
            scenario: 'homepage-items',
            recommendationType: 'ItemsToUser'
        };
        return await this.getRecommendations(request);
    }

    // Fetches personalized event recommendations
    async getPersonalEvents(userId, count) {
        const request = {
            userId: userId,
            count: count,
            scenario: 'personal-items',
            recommendationType: 'ItemsToUser'
        };
        return await this.getRecommendations(request);
    }

    // Fetches popular event recommendations
    async getPopularEvents(userId, count) {
        const request = {
            userId: userId,
            count: count,
            scenario: 'popular-items',
            recommendationType: 'ItemsToUser'
        };
        return await this.getRecommendations(request);
    }

    // Fetches recently viewed event recommendations
    async getRecentylViewedEvents(userId, count) {
        const request = {
            userId: userId,
            count: count,
            scenario: 'recently-viewed-items',
            recommendationType: 'ItemsToUser'
        };
        return await this.getRecommendations(request);
    }

    // Fetches the next set of event recommendations based on a recommendation ID
    async getNextEvents(count, recommId) {
        const request = {
            count: count,
            recommId: recommId,
            recommendationType: 'NextItems'
        };
        return await this.getRecommendations(request);
    }

    // Searches for events based on a query, personalized for a user
    async searchEvents(userId, count, searchQuery) {
        const request = {
            userId: userId,
            count: count,
            searchQuery: searchQuery,
            scenario: 'search-items-personalized',
            recommendationType: 'SearchItems'
        };
        return await this.getRecommendations(request);
    }

    // Handles API response, throwing errors for bad requests or returning data for valid responses

}

const feedService = new FeedService(config.feedBaseUrl);
export default feedService;
