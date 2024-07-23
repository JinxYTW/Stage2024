class askServices {
    constructor() {}

    /**
     * Creates a demande by sending a POST request to the specified API endpoint.
     * @param {Object} data - The data to be sent in the request body.
     * @returns {Promise<Object>} - A promise that resolves to the response JSON.
     * @throws {Error} - If there is a network error or an error occurs during the creation of the demande.
     */
    async createDemande(data) {
        try {
            const response = await fetch('http://127.0.0.1:8080/api/ask', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('Erreur réseau : ' + response.statusText);
            }

            return await response.json();
        } catch (error) {
            throw new Error('Erreur lors de la création de la demande : ' + error.message);
        }
    }
}

export { askServices };