class detailServices{
    constructor() {}

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