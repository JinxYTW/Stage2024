import { homeController } from "./controllers/homeController.js";
import { utilisateurAuthentifie } from "../services/auth.js"; // Importer la fonction utilisateurAuthentifie()
import { SSEClient } from '../libs/sse-client.js';
import { homeServices } from "../services/home-services.js";

const services = new homeServices();

function test(data,myHomeController){
    console.log('Demande ID:', data);
    myHomeController.test();
}

function afficherDemandesUtilisateur(data, myHomeController) {
    console.log('Demandes utilisateur:', data);
    myHomeController.afficherDemandes(data);
}
    

async function run(myHomeController) {
    const baseUrl = "localhost:8080"; 

    const sseClientWaiting = new SSEClient(baseUrl);
    

    try {
        
        await sseClientWaiting.connect();
        

        console.log('Abonnement à l\'événement \'newDemande\'...');
        await sseClientWaiting.subscribe('newDemande', (data) => test(data,myHomeController)).then(()=>console.log('Le SSE Fonctionne !'));
        
        console.log('Abonnement à l\'événement \'demandesUtilisateur\'...');
        await sseClientWaiting.subscribe('demandesUtilisateur', (data) => afficherDemandesUtilisateur(data, myHomeController))
            .then(() => console.log('Abonnement réussi à l\'événement \'demandesUtilisateur\''));

             
        const user_id = window.location.search.split('=')[1];
        await services.fetchDemandesUtilisateur(myHomeController,user_id);

        
        window.addEventListener('unload', () => {
            sseClientWaiting.disconnect();
            console.log('SSE déconnecté avec succès');
        });
        console.log('SSE initialisé avec succès');
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