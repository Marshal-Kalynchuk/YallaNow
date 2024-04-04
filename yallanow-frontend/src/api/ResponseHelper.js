const handleResponse = (response) => {
    if (!response) {
        throw new Error("Response is null.")
    }
    switch (response.status) {
        case 200:
            return response.data;
        case 201:
            return response.data;
        case 204:
            return true;
        case 400:
            throw new Error(response.data?.message || "Bad request.");
        case 401:
            throw new Error("Unauthorized.")
        case 403:
            throw new Error("Access denied.");
        case 404:
            throw new Error("Resource not found.");
        case 409:
            throw new Error(response.data?.message || "Conflict with current state.");
        case 422:
            throw new Error(response.data?.message || "Unprocessable entity.");
        default:
            throw new Error("Error processing request.");
    }
}

export default handleResponse;