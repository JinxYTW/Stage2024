class askServices {
    constructor() {}

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