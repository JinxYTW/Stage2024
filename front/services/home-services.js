class homeServices {
    constructor() {}
    
    
    async searchDemandes(orderNumber, orderDate, orderArticle, orderDomain, orderClient) {
        console.log('Searching demandes with the following parameters:', orderNumber, orderDate, orderArticle, orderDomain, orderClient);
        try {
            const response = await fetch(`http://localhost:8080/api/searchOrders?orderNumber=${encodeURIComponent(orderNumber)}&orderDate=${encodeURIComponent(orderDate)}&orderArticle=${encodeURIComponent(orderArticle)}&orderDomain=${encodeURIComponent(orderDomain)}&orderClient=${encodeURIComponent(orderClient)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            console.log(`Response status: ${response.status}`);
            if (!response.ok) {
                throw new Error('La recherche des demandes a échoué');
            }

            const data = await response.json();
            console.log('Data received:', data);
            return data; // Assurez-vous que l'API renvoie un objet avec une propriété `demandes`
        } catch (error) {
            console.error('Erreur lors de la recherche des demandes:', error);
            return [];
        }
    }

    async fetchDemandesUtilisateur(myHomeController,user_id) {
        const utilisateurId = user_id; 
        try {
            const response = await fetch(`http://localhost:8080/api/demandes/utilisateur?id=${utilisateurId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ utilisateurId })
            });
            console.log(`Response status: ${response.status}`);
            if (response.ok) {
                console.log('Initial demandesUtilisateur emitted');
            } else {
                console.error('Erreur lors de la récupération des demandes utilisateur');
            }
        } catch (error) {
            console.error('Erreur de réseau lors de la récupération des demandes utilisateur:', error);
        }
    }

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

    async generatePdfDevis(devisId) {
        console.log(`Generating PDF for demande ID: ${devisId}`);
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/generatePdfDevis?id=${devisId}`, {
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

    async generatePdfBonCommande(bonCommandeId) {
        console.log(`Generating PDF for bonCommande ID: ${bonCommandeId}`);
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/generatePdfBonCommande?id=${bonCommandeId}`, {
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
        }catch (error) {
            console.log("Erreur dans la génération du PDF", error);
            return null;
        }

}
    async generatePdfFacture(factureId) {
        console.log(`Generating PDF for facture ID: ${factureId}`);
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/generatePdfFacture?id=${factureId}`, {
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
        }catch (error) {
            console.log("Erreur dans la génération du PDF", error);
            return null;
        }
    }









}


export { homeServices };