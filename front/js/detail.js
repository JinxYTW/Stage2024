import { detailController } from "./controllers/detailController.js";
import { utilisateurAuthentifie } from "../services/auth.js";   
import { SSEClient } from '../libs/sse-client.js';
import { ConnectServices } from "../services/connect-services.js";



document.addEventListener("DOMContentLoaded", async () => {
    const connectServices = new ConnectServices();
    console.log('DOM entièrement chargé et analysé');
    // Vérifier si l'utilisateur est authentifié avant de charger la page protégée
    const isAuthenticated = await connectServices.verifyToken();

    if (!isAuthenticated) {
        console.log('Utilisateur non authentifié');
        window.location.href = 'connect.html';
    }  else {
        new detailController();
    }
}
);