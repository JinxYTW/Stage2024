import { detailServices } from "../../services/detail-services.js";
import { homeServices } from "../../services/home-services.js";

class detailView {
    constructor() {
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');

        this.demandeName = document.getElementById('demande_name');
        this.urgenceLevel = document.getElementById('urgence_level');
        this.etatCommande = document.getElementById('etat_commande');
        this.fournisseurNom = document.getElementById('fournisseur_nom');
        this.selectedMarket = document.getElementById('selected_market');
        this.justification = document.getElementById('justification');
        this.etatDevis = document.getElementById('etat_devis');
        this.etatBc = document.getElementById('etat_bc');
        this.etatLivraison = document.getElementById('etat_livraison');
        this.articleSelected = document.getElementById('aricle_selected');
        this.additionalDetails = document.getElementById('additional-details');

        this.detailServices = new detailServices();
        this.homeServices = new homeServices();

        this.updateUserNames();
        this.updateUserRole();
        this.updateDemandeDetails();
    }

    async updateUserNames() {
        const userId = new URLSearchParams(window.location.search).get('user');
        const names = await this.homeServices.getNames(userId);
        this.userName.textContent = names;
    }

    async updateUserRole() {
        const userId = new URLSearchParams(window.location.search).get('user');
        if (userId) {
            const role = await this.homeServices.getRole(userId);
            console.log('Role:', role);
            if (role) {
                this.userRole.textContent = role;
            } else {
                this.userRole.textContent = 'Erreur lors de la récupération du rôle';
            }
        } else {
            console.error('ID utilisateur manquant dans l\'URL');
        }
    }

    /**
     * Updates the demande details on the view.
     * Retrieves the demandeId from the URL parameters and uses it to fetch the demande details.
     * Updates the UI elements with the fetched demande details.
     * If demandeId is missing, displays an alert message.
     * If an error occurs while fetching the demande details, displays an alert message.
     * @returns {Promise<void>} A promise that resolves when the demande details are updated.
     */
    async updateDemandeDetails() {
        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
        if (!demandeId) {
            alert("Identifiant de demande manquant.");
            return;
        }

        const demandeDetails = await this.detailServices.getDemandeDetails(demandeId);



        if (demandeDetails) {

        this.demandeName.textContent = "Demande " + demandeDetails.id; //A modifier apres en rajoutant domainde devant
            this.urgenceLevel.textContent = demandeDetails.urgence;

        // Enlever les classes d'urgence existantes
        this.urgenceLevel.classList.remove('badge-urgency-low', 'badge-urgency-medium', 'badge-urgency-high');

        // Ajouter la classe d'urgence en fonction du niveau
        if (demandeDetails.urgence === 'basse') {
            this.urgenceLevel.classList.add('badge-urgency-low');
        } else if (demandeDetails.urgence === 'moyenne') {
            this.urgenceLevel.classList.add('badge-urgency-medium');
        } else if (demandeDetails.urgence === 'haute') {
            this.urgenceLevel.classList.add('badge-urgency-high');
        }
            this.etatCommande.innerHTML = `<span style="color: red;">${demandeDetails.etat}</span>`;
            this.fournisseurNom.innerHTML = `<span style="color: blue;">${demandeDetails.fournisseur_id}</span>`;
            this.selectedMarket.textContent = demandeDetails.marche;
            this.justification.textContent = demandeDetails.justification;
    
            this.etatDevis.innerHTML = `
                Devis validé par <span style="color: blue;">${demandeDetails.devis.validePar}</span> le <span style="color: blue;">${demandeDetails.devis.date_devis}</span><br>
                Numéro de devis: <span style="color: blue;">${demandeDetails.devis.numero}</span><br>
                Commentaires:<br><a id="pathToDevis" href="${demandeDetails.devis.fichier_pdf}">devis_${demandeDetails.devis.numero}.pdf</a>
            `;
    
            this.etatBc.innerHTML = `
                BC édité par <span style="color: blue;">${demandeDetails.bc.editePar}</span> le <span style="color: blue;">${demandeDetails.bc.date}</span><br>
                Numéro de commande: <span style="color: blue;">${demandeDetails.bc.numero}</span><br>
                Commentaires:<br><a id="pathToBc" href="${demandeDetails.bc.path}">BC_${demandeDetails.bc.numero}.pdf</a>
            `;
    
            this.etatLivraison.innerHTML = `
                Livraison le <span style="color: blue;">${demandeDetails.livraison.date}</span> à <span style="color: blue;">${demandeDetails.livraison.lieu}</span><br>
                BL signé par <span style="color: blue;">${demandeDetails.livraison.signePar}</span><br>
                Numéro ou Nom Transitaire: <span style="color: blue;">${demandeDetails.livraison.transitaire}</span><br>
                Commentaires:<br><a id="pathToFacture" href="${demandeDetails.livraison.path}">BL_${demandeDetails.livraison.numero}.pdf</a>
            `;
    
            this.articleSelected.innerHTML = `<span style="color: blue;">${demandeDetails.quantite}x ${demandeDetails.typeof} ${demandeDetails.marque} ${demandeDetails.reference}</span>`;
            this.additionalDetails.innerHTML = `<span style="color: blue;">${demandeDetails.commentaires}</span>`;
        } else {
            alert("Erreur lors de la récupération des détails de la demande.");
        }
    }
}

export { detailView };
