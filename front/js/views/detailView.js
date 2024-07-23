import { detailServices } from "../../services/detail-services.js";
import { homeServices } from "../../services/home-services.js";

class detailView {
    constructor() {
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');

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

    async updateDemandeDetails() {
        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
        if (!demandeId) {
            alert("Identifiant de demande manquant.");
            return;
        }

        const demandeDetails = await this.detailServices.getDemandeDetails(demandeId);

        if (demandeDetails) {
            this.urgenceLevel.textContent = demandeDetails.urgence;
            this.etatCommande.textContent = demandeDetails.etat;
            this.fournisseurNom.textContent = demandeDetails.fournisseur_id; //A modifier en créant méthode Dao + implémentation de ce nouveau Dao dans la méthode de DemandeController
            this.selectedMarket.textContent = demandeDetails.marche;
            this.justification.textContent = demandeDetails.justification;
            this.etatDevis.innerHTML = `Devis validé par ${demandeDetails.devis.validePar} le ${demandeDetails.devis.date_devis}<br>Numéro de devis: ${demandeDetails.devis.numero}<br>Commentaires:<br><a id="pathToDevis" href="${demandeDetails.devis.fichier_pdf}">devis_${demandeDetails.devis.numero}.pdf</a>`; //A modifier en créant Dao + implémentation de ce nouveau Dao dans la méthode de DemandeController
            this.etatBc.innerHTML = `BC édité par ${demandeDetails.bc.editePar} le ${demandeDetails.bc.date}<br>Numéro de commande: ${demandeDetails.bc.numero}<br>Commentaires:<br><a id="pathToBc" href="${demandeDetails.bc.path}">BC_${demandeDetails.bc.numero}.pdf</a>`;//A modifier en créant Dao + implémentation de ce nouveau Dao dans la méthode de DemandeController
            this.etatLivraison.innerHTML = `Livraison le ${demandeDetails.livraison.date} à ${demandeDetails.livraison.lieu}<br>BL signé par ${demandeDetails.livraison.signePar}<br>Numéro ou Nom Transitaire: ${demandeDetails.livraison.transitaire}<br>Commentaires:<br><a id="pathToFacture" href="${demandeDetails.livraison.path}">BL_${demandeDetails.livraison.numero}.pdf</a>`;//A modifier en créant Dao + implémentation de ce nouveau Dao dans la méthode de DemandeController
            this.articleSelected.textContent = `${demandeDetails.quantite}x${demandeDetails.typeof}${demandeDetails.marque}${demandeDetails.reference}`;
            this.additionalDetails.textContent = demandeDetails.commentaires;//A modifier en créant Dao + implémentation de ce nouveau Dao dans la méthode de DemandeController
        } else {
            alert("Erreur lors de la récupération des détails de la demande.");
        }
    }
}

export { detailView };
