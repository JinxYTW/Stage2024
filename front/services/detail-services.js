class detailServices{
    constructor() {}

    async getGroupesNames(userId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getGroupesNamesByUtilisateurId?utilisateurId=${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération des noms de groupes a échoué');
            }
            const data = await response.json();
            return data;
        }
        catch (error) {
            console.error("Erreur dans la récupération des noms de groupes", error);
            return null;
        }
    }

    /**
     * Retrieves the details of a demande from the server.
     * @param {number} demandeId - The ID of the demande to retrieve details for.
     * @returns {Promise<Object|null>} - A promise that resolves to the details of the demande, or null if an error occurs.
     */
    async getDemandeDetails(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/demande/details?id=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('La récupération des détails de la demande a échoué');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error("Erreur dans la récupération des détails de la demande", error);
            return null;
        }
    }



}

export { detailServices };