import { ConnectServices } from "../../services/connect-services.js"; // Import de la classe ConnectServices
import { utilisateurAuthentifie } from "../../services/auth.js"; // Import de la fonction utilisateurAuthentifie()

class connectView{
    
    constructor(){
        console.log("connectView")
        this.usernameField=document.getElementById('usernameco');
        this.passwordField=document.getElementById('passwordco');
        this.submitButton=document.getElementById('submitco');


        this.connectServices = new ConnectServices();

        
        };

        bindLogin(){
            this.submitButton.addEventListener('click', (event) => {
                event.preventDefault(); // Empêche la soumission du formulaire par défaut
                console.log("ok")
                this.handleLogin();
            })
        }
    
        async handleLogin() {
            console.log("ok")
            const username = this.usernameField.value;
            const password = this.passwordField.value;
    
            try {
                const result = await this.connectServices.login(username, password);
    
                if (result.success) {
                    console.log('Connexion réussie :', result);
                    // Vérifier si l'utilisateur est authentifié après avoir reçu le succès de la connexion
                    if (utilisateurAuthentifie()) {
                        console.log('Utilisateur authentifié');
                        // Rediriger vers la page protégée (home.html) avec l'identifiant de l'utilisateur
                        window.location.href = `home.html?user=${result.id}`;
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

    


export {connectView};   