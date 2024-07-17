import { homeController } from "./controllers/homeController.js";
import { utilisateurAuthentifie } from "../services/auth.js"; // Importer la fonction utilisateurAuthentifie()
import { SSEClient } from '../libs/sse-client.js';

function test(data,myHomeController){
    console.log('Demande ID:', data);
    myHomeController.test();
}
    

async function run(myHomeController) {
    const baseUrl = "localhost:8080"; 

    const sseClientWaiting = new SSEClient(baseUrl);
    

    try {
        console.log('Tentative de connexion et d\'abonnement à l\'événement SSE...');
        await sseClientWaiting.connect();
        console.log('Connexion SSE réussie');

        console.log('Abonnement à l\'événement \'newDemande\'...');
        // Abonnement à l'événement 'newDemande'
        await sseClientWaiting.subscribe('newDemande', (data) => test(data,myHomeController)).then(()=>console.log('Le SSE Fonctionne !'));
            // Mettez à jour l'interface utilisateur avec les données de la nouvelle demande
            // Exemple : ajouter la demande à la liste des demandes récentes sur votre page
        
        console.log('Abonnement réussi à l\'événement \'newDemande\'');

        
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
        const myHomeController =new homeController();
        run(myHomeController);
    }
});