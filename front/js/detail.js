import { detailController } from "./controllers/detailController.js";
import { utilisateurAuthentifie } from "../services/auth.js";   
import { SSEClient } from '../libs/sse-client.js';


document.addEventListener("DOMContentLoaded", () => {
    console.log('DOM entièrement chargé et analysé');
    // Vérifier si l'utilisateur est authentifié avant de charger la page protégée
    if (!utilisateurAuthentifie()) {
        console.log('Utilisateur non authentifié');
        window.location.href = 'connect.html';
    } else {
        new detailController();
    }
}
);