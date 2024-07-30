class detailServices{
    constructor() {}

    async uploadFiles(demandeId,formData) {
        try {

            // Afficher le contenu de formData pour déboguer
        for (let [key, value] of formData.entries()) {
            if (value instanceof File) {
                console.log(`Champ: ${key}, Fichier: ${value.name}`);
            } else {
                console.log(`Champ: ${key}, Valeur: ${value}`);
            }
        }
            // Créer une instance de `Headers`
            const headers = new Headers();
            
            // Supposer que le nom du fichier est disponible dans le `formData`
            // Vous devez ajuster cette partie selon votre logique pour obtenir le nom du fichier
            const fileName = formData.get('files[]').name;
            headers.append('filename', fileName);
            
            const response = await fetch(`http://127.0.0.1:8080/uploadDevis?demandeId=${demandeId}`, {
                method: 'POST',
                body: formData,
                headers: headers
            });

            
            if (response.ok) {
                console.log('Fichiers téléchargés avec succès');
                return true;
            } else {
                console.error('Erreur lors du téléchargement des fichiers');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }
    

    async updateNotificationType(demandeId, newType) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/updateNotificationType`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify({ demandeId, newType }) // Envoi des données nécessaires
            });
    
            if (response.ok) {
                console.log('Notification mise à jour avec succès');
                return true;
            } else {
                console.error('Erreur lors de la mise à jour de la notification');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

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
            if (data.status === 'success') {
                return data.groupes; // Liste des groupes
            } else {
                throw new Error('Erreur dans la réponse de l\'API');
            }
        }
        catch (error) {
            console.error("Erreur dans la récupération des noms de groupes", error);
            return [];
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