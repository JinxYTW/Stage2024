import { askServices } from "../../services/ask-services.js";

class askView{
    constructor(){

    this.askServices = new askServices();
    this.initializeForm();
    }

    initializeForm() {
        const askButton = document.getElementById('ask_button');
        askButton.addEventListener('click', async (event) => {
            event.preventDefault();
    
            // Récupérer les valeurs des champs du formulaire
            const project_switch = document.getElementById('project_switch');
            const project_name = document.getElementById('project_name').value;
            const project_domain = document.getElementById('project_domain').value;

            const referent_select = document.getElementById('referent_select').value;
            const urgency_level = document.querySelector('.btn-outline-success.btn.active')?.textContent;
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
    
            // Vérifier si project_switch existe avant d'accéder à sa propriété checked
            const project_switch_checked = project_switch ? project_switch.checked : false;
    
            // Créer un objet avec les données du formulaire
            const formData = {
                project_switch: project_switch_checked,
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
            console.log('Données du formulaire:', formData);
    
            try {
                // Appeler le service pour créer la demande
                const response = await this.askServices.createDemande(formData);
                console.log('Demande créée avec succès:', response);
    
                // Afficher un message de succès ou rediriger l'utilisateur
                alert('Demande créée avec succès. ID: ' + response.demandeId);
                // Redirection vers une autre page après la création réussie
                window.location.href = 'home.html'; // Remplacez par votre URL de destination
    
            } catch (error) {
                console.error('Erreur lors de la création de la demande:', error.message);
                alert('Erreur lors de la création de la demande : ' + error.message);
            }
        });
    }

    
}

export {askView};