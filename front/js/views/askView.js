import { askServices } from "../../services/ask-services.js";
import { homeServices } from "../../services/home-services.js";

class askView{
    constructor(){

    this.askServices = new askServices();
    this.homeServices = new homeServices();

    this.userName = document.getElementById('user_nomprenom');
    this.userRole = document.getElementById('user_role');

    this.updateUserNames();
    this.updateUserRole();
    this.initializeForm();
    }

    getUserIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('user');
    }

    initializeForm() {
        const askButton = document.getElementById('ask_button');
        const projectSwitch = document.getElementById('project-switch');
        const projectName = document.getElementById('project_name');
        const projectDomain = document.getElementById('project_domain');
        const urgencyButtons = document.getElementById('urgency-buttons');
    
        // Fonction pour verrouiller/déverrouiller les champs project_name et project_domain
        const toggleProjectFields = () => {
            const isProjectEnabled = projectSwitch.checked;
            projectName.disabled = !isProjectEnabled;
            projectDomain.disabled = !isProjectEnabled;
        };
    
        // Ajouter un écouteur d'événement pour le changement de l'état de projectSwitch
        projectSwitch.addEventListener('change', toggleProjectFields);
    
        // Initialiser l'état des champs project_name et project_domain
        toggleProjectFields();
    
        // Ajouter des écouteurs d'événements pour les boutons d'urgence
        urgencyButtons.addEventListener('click', (event) => {
            if (event.target.tagName === 'BUTTON') {
                // Supprimer la classe active de tous les boutons
                urgencyButtons.querySelectorAll('button').forEach(button => button.classList.remove('active'));
    
                // Ajouter la classe active au bouton cliqué
                event.target.classList.add('active');
            }
        });
    
        askButton.addEventListener('click', async (event) => {
            event.preventDefault();

            const utilisateur_id = this.getUserIdFromUrl();
    
            // Récupérer les valeurs des champs du formulaire
            const project_switch = projectSwitch.checked;
            const project_name = projectName.value;
            const project_domain = projectDomain.value;
            const referent_select = document.getElementById('referent_select').value;
            const urgency_level = document.querySelector('#urgency-buttons .btn.active')?.textContent;
            const aricle_select = document.getElementById('aricle_select').value;
            const quantity = parseInt(document.getElementById('quantity').value);
            const brand = document.getElementById('brand').value;
            const site = document.getElementById('site').value;
            const reference = document.getElementById('reference').value;
            const details_w = document.getElementById('details_w').value;
            const details = document.getElementById('details').value;
            const market_select = document.getElementById('market_select').value;
            const justification = document.getElementById('justification').value;
            const additional_details = document.getElementById('additional-details').value;
    
            // Créer un objet avec les données du formulaire
            const formData = {
                utilisateur_id,
                project_switch,
                project_name,
                project_domain,
                referent_select,
                urgency_level,
                aricle_select,
                quantity,
                brand,
                site,
                reference,
                details_w,
                details,
                market_select,
                justification,
                additional_details
            };
    
            
    
            try {
                // Appeler le service pour créer la demande
                const response = await this.askServices.createDemande(formData);
                console.log('Demande créée avec succès:', response);
    
                // Afficher un message de succès ou rediriger l'utilisateur
                alert('Demande créée avec succès. ID: ' + response.demandeId);
                // Redirection vers une autre page après la création réussie
                const id = this.getUserIdFromUrl();
                window.location.href = `home.html?user=${id}`; // Remplacez par votre URL de destination
    
            } catch (error) {
                console.error('Erreur lors de la création de la demande:', error.message);
                alert('Erreur lors de la création de la demande : ' + error.message);
            }
        });
    }  

    async updateUserNames(){
        const userId = window.location.search.split('=')[1];
        const names = await this.homeServices.getNames(userId);
        this.userName.textContent = names;
    
    }

    async updateUserRole() {
        const userId = new URLSearchParams(window.location.search).get('user');
        if (userId) {
            const role = await this.homeServices.getRole(userId);
            
            if (role) {
                this.userRole.textContent = role;
            } else {
                this.userRole.textContent = 'Erreur lors de la récupération du rôle';
            }
        } else {
            console.error('ID utilisateur manquant dans l\'URL');
        }
    }
    

    
}

export {askView};