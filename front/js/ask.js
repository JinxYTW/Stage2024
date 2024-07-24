import { askController } from "./controllers/askController.js"; // Importer la classe askController
import { utilisateurAuthentifie } from "../services/auth.js"; // Importer la fonction utilisateurAuthentifie()
import { ConnectServices } from "../services/connect-services.js";



document.addEventListener("DOMContentLoaded", async () => {
    const connectServices = new ConnectServices();
    console.log('DOM entièrement chargé et analysé');
    // Vérifier si l'utilisateur est authentifié avant de charger la page protégée
    const isAuthenticated = await connectServices.verifyToken();

    if (!isAuthenticated) {
        console.log('Utilisateur non authentifié');
        window.location.href = 'connect.html';
    } else {
        new askController();
    }
});