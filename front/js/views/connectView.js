import { ConnectServices } from "../../services/connect-services.js";
import { utilisateurAuthentifie } from "../../services/auth.js";

class connectView {
    constructor() {
        console.log("Initializing connectView");
        this.usernameField = document.getElementById('usernameco');
        this.passwordField = document.getElementById('passwordco');
        this.submitButton = document.getElementById('submitco');

        this.connectServices = new ConnectServices();
    }

    bindLogin() {
        this.submitButton.addEventListener('click', (event) => {
            event.preventDefault(); // Empêche la soumission du formulaire par défaut
            console.log("Submit button clicked");
            this.handleLogin();
        });
    }

    async handleLogin() {
        console.log("Handling login");
        const username = this.usernameField.value;
        console.log("Username:", username);
        const password = this.passwordField.value;
        console.log("password:",password);

        try {
            const result = await this.connectServices.login(username, password);
            console.log("Login result:", result);

            if (result && result.status === 'success') {
                console.log('Connexion réussie :', result);
                localStorage.setItem('jwtToken', result.token); // Stocker le token JWT dans le localStorage
                if (utilisateurAuthentifie()) {
                    console.log('Utilisateur authentifié');
                    window.location.href = `home.html?user=${result.userId}`;
                } else {
                    console.error('Utilisateur non authentifié malgré la connexion réussie.');
                }
            } else {
                console.error('Identifiants incorrects');
            }
        } catch (error) {
            console.error('Erreur lors de la connexion :', error);
        }
    }
}

export { connectView };
