import { homeController } from "./controllers/homeController.js";
import { utilisateurAuthentifie } from "../services/auth.js"; // Importer la fonction utilisateurAuthentifie()
import { SSEClient } from '../libs/sse-client.js';

async function run() {
    const baseUrl = "localhost:8080"; 

    const sseClientWaiting = new SSEClient(baseUrl);
    console.log('SSEClient:', sseClientWaiting);

    try {
        console.log('Tentative de connexion et d\'abonnement à l\'événement SSE...');
        await sseClientWaiting.connect();
        console.log('Connexion SSE réussie');

        // Abonnement à l'événement 'newDemande'
        await sseClientWaiting.subscribe('newDemande', (data) => {
            console.log('Nouvelle demande créée :', data);
            // Mettez à jour l'interface utilisateur avec les données de la nouvelle demande
            // Exemple : ajouter la demande à la liste des demandes récentes sur votre page
        });

        
        window.addEventListener('unload', () => {
            sseClientWaiting.disconnect();
        });
    } catch (error) {
        console.error("Échec de la connexion ou de l'abonnement SSE :", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    console.log('DOM entièrement chargé et analysé');
    // Vérifier si l'utilisateur est authentifié avant de charger la page protégée
    if (!utilisateurAuthentifie()) {
        console.log('Utilisateur non authentifié');
        window.location.href = 'connect.html';
    } else {
        new homeController();
        run();
    }
});