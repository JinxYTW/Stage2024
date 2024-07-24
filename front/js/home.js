import { homeController } from "./controllers/homeController.js";
import { utilisateurAuthentifie } from "../services/auth.js"; // Importer la fonction utilisateurAuthentifie()
import { SSEClient } from '../libs/sse-client.js';
import { homeServices } from "../services/home-services.js";
import { ConnectServices } from "../services/connect-services.js";

const services = new homeServices();
const myHomeController =new homeController();
const connectServices = new ConnectServices();

function test(data,myHomeController){
    console.log('Demande ID:', data);
    myHomeController.test();
}

function afficherDemandesUtilisateur(data) {
    console.log('Demandes utilisateur:', data);
    myHomeController.setDemandes(data);
    updateDemandesForCurrentMonth();
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
function updateDemandesForCurrentMonth() {
    const currentMonthElement = document.getElementById('currentMonth');
    const currentDate = new Date(currentMonthElement.dataset.date);
    const filteredDemandes = myHomeController.filterDemandesByMonth(currentDate.getMonth(), currentDate.getFullYear());
    myHomeController.afficherDemandes(filteredDemandes);
}

function changeMonth(offset) {
    const currentMonthElement = document.getElementById('currentMonth');
    const currentDate = new Date(currentMonthElement.dataset.date);
    currentDate.setMonth(currentDate.getMonth() + offset);
    currentMonthElement.dataset.date = currentDate.toISOString();
    currentMonthElement.textContent = currentDate.toLocaleString('default', { month: 'long', year: 'numeric' });
    updateDemandesForCurrentMonth();
}



document.addEventListener("DOMContentLoaded", async () => {
    console.log('DOM entièrement chargé et analysé');
    
    // Vérifier si l'utilisateur est authentifié avant de charger la page protégée
    const isAuthenticated = await connectServices.verifyToken();

    if (!isAuthenticated) {
        console.log('Utilisateur non authentifié');
        window.location.href = 'connect.html';
    } else {
        const currentMonthElement = document.getElementById('currentMonth');
        currentMonthElement.dataset.date = new Date().toISOString();
        currentMonthElement.textContent = new Date().toLocaleString('default', { month: 'long', year: 'numeric' });

        document.getElementById('prevMonth').addEventListener('click', () => changeMonth(-1));
        document.getElementById('nextMonth').addEventListener('click', () => changeMonth(1));

        run(myHomeController);
    }
});