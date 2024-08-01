class detailServices{
    constructor() {}

    async changeSignataireNameThanksToUserId(userId, pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/changeSignataireNameThanksToUserId`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify({ userId, pdfPath }) // Envoi des données nécessaires
            });
    
            if (response.ok) {
                console.log('Valideur mise à jour avec succès');
                return true;
            } else {
                console.error('Erreur lors de la mise à jour du valideur');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

    async changeEditeurNameThanksToUserId(userId, pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/changeEditeurNameThanksToUserId`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify({ userId, pdfPath }) // Envoi des données nécessaires
            });
    
            if (response.ok) {
                console.log('Valideur mise à jour avec succès');
                return true;
            } else {
                console.error('Erreur lors de la mise à jour du valideur');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

    async changeValideurNameThanksToUserId(userId, pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/changeValideurNameThanksToUserId`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify({ userId, pdfPath }) // Envoi des données nécessaires
            });
    
            if (response.ok) {
                console.log('Valideur mise à jour avec succès');
                return true;
            } else {
                console.error('Erreur lors de la mise à jour du valideur');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

    async updateDemandeEtat(demandeId, newEtat) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/updateDemandeEtat`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify({ demandeId, newEtat }) // Envoi des données nécessaires
            });
    
            if (response.ok) {
                console.log('Demande mise à jour avec succès');
                return true;
            } else {
                console.error('Erreur lors de la mise à jour de la demande');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

    async isOneInvoiceValidate(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/isOneInvoiceValidate?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log(data);
                return data.isOneInvoiceValidate; 
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async getInvoicePaths(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getInvoicePaths?demandeId=${demandeId}`);
            if (response.ok) {
                const data = await response.json();
                return data; // Assurez-vous que `pdfPaths` est le nom correct dans votre réponse JSON
            } else {
                console.error('Erreur lors de la récupération des bons de commande');
                return [];
            }
        } catch (error) {
            console.error('Erreur:', error);
            return [];
        }
    }

    async validateInvoice(pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/validateInvoice`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ pdfPath })
            });
    
            if (response.ok) {
                console.log('Facture validée avec succès');
                return true;
            } else {
                console.error('Erreur lors de la validation de la facture');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }

    async getFactureCountFromDemandId(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getFactureCountFromDemandId?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                return data.factureCount; // Supposons que la réponse contient { count: nombreDeDevis }
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async uploadInvoiceFiles(demandeId, formData) {
        try {
            // Afficher le contenu de formData pour déboguer
            for (let [key, value] of formData.entries()) {
                if (value instanceof File) {
                    console.log(`Champ: ${key}, Fichier: ${value.name}`);
                } else {
                    console.log(`Champ: ${key}, Valeur: ${value}`);
                }
            }
    
            // Créer une instance de Headers
            const headers = new Headers();
    
            // Supposer que le nom du fichier est disponible dans le formData
            // Vous devez ajuster cette partie selon votre logique pour obtenir le nom du fichier
            if (formData.has('invoiceFile')) {
                const file = formData.get('invoiceFile');
                headers.append('filename', file.name);
            }
    
            // Envoyer les données au serveur
            const response = await fetch(`http://127.0.0.1:8080/api/uploadInvoice?demandeId=${demandeId}`, {
                method: 'POST',
                body: formData,
                headers: headers // Ajouter les headers si nécessaire
            });
    
            if (response.ok) {
                console.log('Facture téléversée avec succès');
                return true;
            } else {
                console.error('Erreur lors du téléversement de la facture');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }
    
    

    async isOneNotifOnState(demandeId, type) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/isOneNotifOnState?demandeId=${demandeId}&type=${type}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log(data);
                return data.isOneNotifOnState;
            } else {
                console.error('Erreur lors de la récupération du nombre de notifications');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async isOneBcValidate(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/isOneBcValidate?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log(data);
                return data.isOneBcValidate; 
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async getBcPaths(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getBcPdfPath?demandeId=${demandeId}`);
            if (response.ok) {
                const data = await response.json();
                return data; // Assurez-vous que `pdfPaths` est le nom correct dans votre réponse JSON
            } else {
                console.error('Erreur lors de la récupération des bons de commande');
                return [];
            }
        } catch (error) {
            console.error('Erreur:', error);
            return [];
        }
    }
    
    // Service pour valider un bon de commande
    async validateBc(pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/validateBc`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ pdfPath: pdfPath })
            });
    
            if (response.ok) {
                console.log('Bon de commande validé avec succès');
                return true;
            } else {
                console.error('Erreur lors de la validation du bon de commande');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }
    

    async getBcCountFromDemandId(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getBcCountFromDemandId?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                return data.bcCount; // Supposons que la réponse contient { count: nombreDeDevis }
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async uploadBcFiles(demandeId, formData) {
        try {
            // Afficher le contenu de formData pour déboguer
            for (let [key, value] of formData.entries()) {
                if (value instanceof File) {
                    console.log(`Champ: ${key}, Fichier: ${value.name}`);
                } else {
                    console.log(`Champ: ${key}, Valeur: ${value}`);
                }
            }
    
            // Créer une instance de Headers
            const headers = new Headers();
    
            // Supposer que le nom du fichier est disponible dans le formData
            // Vous devez ajuster cette partie selon votre logique pour obtenir le nom du fichier
            if (formData.has('bcFile')) {
                const file = formData.get('bcFile');
                headers.append('filename', file.name);
            }
    
            // Envoyer les données au serveur
            const response = await fetch(`http://127.0.0.1:8080/api/uploadBc?demandeId=${demandeId}`, {
                method: 'POST',
                body: formData,
                headers: headers // Ajouter les headers si nécessaire
            });
    
            if (response.ok) {
                console.log('Bon de commande téléversé avec succès');
                return true;
            } else {
                console.error('Erreur lors du téléversement du bon de commande');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }
    
    

    async validateDevis(pdfPath) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/validateDevis`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ pdfPath: pdfPath }) // Envoyer le chemin du PDF dans le corps de la requête
            });
    
            if (response.ok) {
                const result = await response.json();
                console.log(result);
                return result.success; // On suppose que la réponse contient une propriété "success"
            } else {
                console.error('Erreur lors de la validation du devis');
                return false;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return false;
        }
    }
    

    async getDevisPdfPath(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getDevisPdfPath?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                return data; 
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async isOneDevisValidate(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/isOneDevisValidate?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                console.log(data);
                return data.isOneDevisValidate; 
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }

    async getDevisCount(demandeId) {
        try {
            const response = await fetch(`http://127.0.0.1:8080/api/getDevisCount?demandeId=${demandeId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                const data = await response.json();
                return data.devisCount; // Supposons que la réponse contient { count: nombreDeDevis }
            } else {
                console.error('Erreur lors de la récupération du nombre de devis');
                return 0;
            }
        } catch (error) {
            console.error('Erreur:', error);
            return 0;
        }
    }
    

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