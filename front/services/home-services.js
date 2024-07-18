class homeServices {
    constructor() {}

    

    async getNames(userId){
        try {
            console.log(`Fetching names for user ID: ${userId}`);

            const response = await fetch(`http://127.0.0.1:8080/api/getNames/${userId}`,{
                method : 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
    
            });
            console.log(`Response status: ${response.status}`);
            if(!response.ok){
                throw new Error('La récupération des noms a échoué');
            }
            const data = await response.json();
            console.log('Data received:', data);
            return data;
    
            
        } catch (error) {
            console.log("Erreur dans la récupération des noms", error);
            return "";
            
        }
    }

    async getRole(userId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getRole/${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération du rôle a échoué');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error("Erreur dans la récupération du rôle", error);
            return "";
        }
    }

    async generatePdfDemande(demandeId) {
        console.log(`Generating PDF for demande ID: ${demandeId}`);
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/generatePdfDemande?id=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            console.log(`Response status: ${response.status}`); // Ajoutez ce log
            
            if (!response.ok) {
                throw new Error('La génération du PDF a échoué');
            }
            const data = await response.json();
            console.log('Data received:', data); // Ajoutez ce log
            return data.pdfPath;
        } catch (error) {
            console.log("Erreur dans la génération du PDF", error);
            return null;
        }
    }

}


export { homeServices };