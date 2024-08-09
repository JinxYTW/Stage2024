/**
 * Represents a class that provides various services related to home.
 */
class homeServices {
    constructor() {}

//Test pour implémentation nouveau système de notification *************************************************
    async updateNotificationTypeReadForUser(notifId,userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/updateNotificationTypeReadForUser?notifId=${notifId}&userId=${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.status !== 200) {
                throw new Error('Échec de la mise à jour de la notification');
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error("Erreur dans la mise à jour de la notification", error);
            return false;
        }
    }

    async markNotifAsReadForUser(notifId,userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/markNotifAsReadForUser?notifId=${notifId}&userId=${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La mise à jour de la notification a échoué');
            }
            return true;
        }
        catch (error) {
            console.error("Erreur dans la mise à jour de la notification", error);
            return false;
        }
    }

    async countUnreadNotificationsForUser(userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/countUnreadNotificationsForUser?userId=${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération des notifications a échoué');
            }
            let data = await response.json();
            
            data = data.count;
            
            return data;
        }
        catch (error) {
            console.error("Erreur dans la récupération des notifications", error);
            return [];
        }
    }

    async getMostImportantNotificationForUser(userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/getMostImportantNotificationForUser?userId=${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération des notifications a échoué');
            }
            const data = await response.json();
            return data;
        }
        catch (error) {
            console.error("Erreur dans la récupération des notifications", error);
            return [];
        }
    }

//*********************************************************************************** */
    

    
    async getOldestUrgentNotification(userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/getOldestUrgentNotification?userId=${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération des notifications a échoué');
            }
            const data = await response.json();
            return data;
        }
        catch (error) {
            console.error("Erreur dans la récupération des notifications", error);
            return [];
        }
    }

    async getNotificationsForUser(userId) {
        try {
            const response = await fetch(`http://localhost:8080/api/getNotificationsForUser?userId=${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('La récupération des notifications a échoué');
            }
            const data = await response.json();
            return data;
        }
        catch (error) {
            console.error("Erreur dans la récupération des notifications", error);
            return [];
        }
    }
    
    
    /**
     * Searches demandes based on the provided parameters.
     * @param {string} orderNumber - The order number to search for.
     * @param {string} orderDate - The order date to search for.
     * @param {string} orderArticle - The order article to search for.
     * @param {string} orderDomain - The order domain to search for.
     * @param {string} orderClient - The order client to search for.
     * @returns {Promise<Array>} - A promise that resolves to an array of demandes matching the search criteria.
     */
    async searchDemandes(orderNumber, orderDate, orderArticle, orderDomain, orderClient) {
        console.log('Searching demandes with the following parameters:', orderNumber, orderDate, orderArticle, orderDomain, orderClient);
        try {
            const response = await fetch(`http://localhost:8080/api/searchOrders?orderNumber=${encodeURIComponent(orderNumber)}&orderDate=${encodeURIComponent(orderDate)}&orderArticle=${encodeURIComponent(orderArticle)}&orderDomain=${encodeURIComponent(orderDomain)}&orderClient=${encodeURIComponent(orderClient)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            
            if (!response.ok) {
                throw new Error('La recherche des demandes a échoué');
            }

            const data = await response.json();
            
            return data; // Assurez-vous que l'API renvoie un objet avec une propriété `demandes`
        } catch (error) {
            console.error('Erreur lors de la recherche des demandes:', error);
            return [];
        }
    }

    /**
     * Fetches demandes utilisateur from the server.
     * @param {Object} myHomeController - The instance of the home controller.
     * @param {string} user_id - The ID of the user.
     * @returns {Promise<void>} - A promise that resolves when the demandes utilisateur are fetched successfully.
     */
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
            
            if (response.ok) {
                console.log('Initial demandesUtilisateur emitted');
            } else {
                console.error('Erreur lors de la récupération des demandes utilisateur');
            }
        } catch (error) {
            console.error('Erreur de réseau lors de la récupération des demandes utilisateur:', error);
        }
    }

    /**
     * Fetches names for a given user ID.
     * @param {number} userId - The ID of the user.
     * @returns {Promise<Array>} - A promise that resolves to an array of names.
     */
    async getNames(userId){
        try {
            

            const response = await fetch(`http://127.0.0.1:8080/api/getNames/${userId}`,{
                method : 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
    
            });
            
            if(!response.ok){
                throw new Error('La récupération des noms a échoué');
            }
            const data = await response.json();
            
            return data;
    
            
        } catch (error) {
            console.log("Erreur dans la récupération des noms", error);
            return "";
            
        }
    }

    /**
     * Retrieves the role for a given user ID.
     * @param {number} userId - The ID of the user.
     * @returns {Promise<string>} A promise that resolves to the role of the user.
     * @throws {Error} If the retrieval of the role fails.
     */
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

    /**
     * Generates a PDF for a given demande ID.
     * @param {number} demandeId - The ID of the demande.
     * @returns {Promise<string|null>} - A promise that resolves to the path of the generated PDF, or null if an error occurs.
     */
    async generatePdfDemande(demandeId) {
        console.log(`Generating PDF for demande ID: ${demandeId}`);
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/generatePdfDemande?id=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            
            if (!response.ok) {
                throw new Error('La génération du PDF a échoué');
            }
            const data = await response.json();
            return data.pdfPath;
        } catch (error) {
            console.log("Erreur dans la génération du PDF", error);
            return null;
        }
    }

    
    









}


export { homeServices };