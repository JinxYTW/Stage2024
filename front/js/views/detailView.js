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
        this.updateActionButtons();
        
        this.addClickableZoneListener();
        this.loadNotifications();
    }

  
   getDemandeIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('user');
}
 //----------------- Gère l'affichage des Notifications -----------------//

 async loadNotifications() {
    const userId = this.getDemandeIdFromUrl();
    if (userId) {
        try {
            const notificationCount = await this.homeServices.countUnreadNotificationsForUser(userId);
            const lastUrgentNotification = await this.homeServices.getMostImportantNotificationForUser(userId);

            // Mettre à jour l'élément HTML avec le nombre de notifications
            const notifZone = document.getElementById('notif_zone');
            notifZone.textContent = `Notifications (${notificationCount})`;

            // Mettre à jour l'élément HTML avec la dernière notification urgente
            const lastNotif = document.getElementById('last_notif');
            if (lastUrgentNotification && lastUrgentNotification.message) {
                lastNotif.textContent = `${lastUrgentNotification.message} ${lastUrgentNotification.demandeId} est à l'état ${lastUrgentNotification.type}`;
                lastNotif.dataset.id = lastUrgentNotification.id; // Ajouter un ID pour le traitement ultérieur
            } else {
                lastNotif.textContent = 'Aucune notification urgente';
                lastNotif.removeAttribute('data-id'); // Assurez-vous de supprimer l'ID s'il n'y a pas de notification
            }
        } catch (error) {
            console.error("Erreur dans la récupération des notifications", error);
        }
    } else {
        console.error('User ID not found in the URL.');
    }
}
// Méthode pour ajouter un événement de clic à la zone cliquable
addClickableZoneListener() {
    const clickableZone = document.querySelector('.clickable-zone');
    clickableZone.addEventListener('click', () => {
        this.handleClickableZoneClick();
    });
}



async handleClickableZoneClick() {
    const notifId = document.getElementById('last_notif').dataset.id;
    const userId = new URLSearchParams(window.location.search).get('user');
    if (notifId && userId) {
        // Marquer la notification comme lue
       
        const success = await this.homeServices.markNotifAsReadForUser(notifId, userId);
        console.log('Notification marked as read:', success);
        if (success) {
            
            const updateSuccess = await this.homeServices.updateNotificationTypeReadForUser(notifId, userId);
            console.log('Notification updated:', updateSuccess);
            if (updateSuccess) {
                // Recharger les notifications
                this.loadNotifications();
            } else {
                console.error('Erreur lors de la mise à jour de la notification');
            }
        } else {
            console.error('Erreur lors du marquage de la notification comme lue');
        }
    } else {
        console.error('ID de notification ou ID utilisateur manquant');
    }
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
                Commentaires:<br><a id="pathToDevis" href="${demandeDetails.devis.fichier_pdf}">devisValide_${demandeDetails.devis.numero}.pdf</a>
            `;
    
            this.etatBc.innerHTML = `
                BC édité par <span style="color: blue;">${demandeDetails.bc.editePar}</span> le <span style="color: blue;">${demandeDetails.bc.date}</span><br>
                Numéro de bon de commande: <span style="color: blue;">${demandeDetails.bc.numero}</span><br>
                Commentaires:<br><a id="pathToBc" href="${demandeDetails.bc.path}">BCValide_${demandeDetails.bc.numero}.pdf</a>
            `;
    
            this.etatLivraison.innerHTML = `
                Livraison le <span style="color: blue;">${demandeDetails.livraison.date}</span> à <span style="color: blue;">${demandeDetails.livraison.lieu}</span><br>
                BL signé par <span style="color: blue;">${demandeDetails.livraison.signePar}</span><br>
                Numéro ou Nom Transitaire: <span style="color: blue;">${demandeDetails.livraison.transitaire}</span><br>
                Commentaires:<br><a id="pathToFacture" href="${demandeDetails.livraison.path}">BLSigne_${demandeDetails.livraison.numero}.pdf</a>
            `;
    
            this.articleSelected.innerHTML = `<span style="color: blue;">${demandeDetails.quantite}x ${demandeDetails.typeof} ${demandeDetails.marque} ${demandeDetails.reference}</span>`;
            this.additionalDetails.innerHTML = `<span style="color: blue;">${demandeDetails.commentaires}</span>`;
        } else {
            alert("Erreur lors de la récupération des détails de la demande.");
        }
    }
    
    async updateActionButtons() {
        const userId = new URLSearchParams(window.location.search).get('user');
    
        if (!userId) {
            console.error('ID utilisateur manquant dans l\'URL');
            return;
        }
    
        try {
            const groupes = await this.detailServices.getGroupesNames(userId);
            const actionsButtonDiv = document.getElementById('actions_button');
            actionsButtonDiv.querySelectorAll('.dynamic-button').forEach(button => button.remove());
    
            const buttonLabels = {
                'treatDevis': 'Traiter les devis',
                'validateDevis': 'Valider les devis',
                'treatBc': 'Traiter les BC',
                'validateBc': 'Valider les BC',
                'notifBcSend': 'Notifier l\'envoi des BC',
                'inventory': 'Depot de la facture'
            };
    
            const buttonActions = {
                'treatDevis': this.openUploadModal.bind(this),
                'validateDevis': this.openValidateModal.bind(this),
                'treatBc': this.openBcUploadModal.bind(this),
                'validateBc': this.openBcValidationModal.bind(this),
                'notifBcSend': this.notifyBcSend.bind(this),
                'inventory': {
                    'button1': this.openInvoiceUploadModal.bind(this),
                    'button2': this.openInvoiceValidationModal.bind(this)
                }
            };
    
            const validGroupes = groupes.filter(group => buttonLabels.hasOwnProperty(group));
            console.log('Groupes valides:', validGroupes);
    
            for (const groupName of validGroupes) {
                if (groupName === 'inventory') {
                    await this.createInventoryButtons(actionsButtonDiv, buttonActions);
                } else {
                    await this.createActionButton(actionsButtonDiv, groupName, buttonLabels, buttonActions);
                }
            }
        } catch (error) {
            console.error('Erreur lors de la récupération des groupes:', error);
        }
    }
    
    
    async createActionButton(container, groupName, labels, actions) {
        const button = document.createElement('button');
        button.className = 'btn btn-info btn-block dynamic-button';
        button.textContent = labels[groupName] || `Action pour ${groupName}`;
        button.setAttribute('data-id', groupName);
    
        button.addEventListener('click', () => {
            if (actions[groupName]) {
                actions[groupName]();
            } else {
                console.log(`Aucune action définie pour le groupe : ${groupName}`);
            }
        });
    
        await this.updateButtonState(button, groupName);
    
        container.appendChild(button);
    }
    
    async createInventoryButtons(container, buttonActions) {
        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    
        const [factureCount, etat, invoiceCount] = await Promise.all([
            this.detailServices.getFactureCountFromDemandId(demandeId),
            this.detailServices.getEtatDemande(demandeId),
            this.detailServices.isOneInvoiceValidate(demandeId)
        ]);
    
        const createButton = (id, text, action, condition) => {
            const button = document.createElement('button');
            button.className = 'btn btn-info btn-block dynamic-button';
            button.textContent = text;
            button.setAttribute('data-id', id);
    
            button.addEventListener('click', action);
    
            if (condition) {
                button.disabled = true;
                button.textContent = condition;
            }
    
            container.appendChild(button);
            // Vérifier l'état du bouton si nécessaire
        this.updateButtonState(button, id);
        };
    
        createButton('inventory-button1', 'Depot de la facture', buttonActions['inventory']['button1'], factureCount >= 1 ? "Facture déjà traité" : null);
        createButton('inventory-button2', 'Validation de la facture', buttonActions['inventory']['button2'], invoiceCount >= 1 ? "Facture déjà validé" : null);
    }
    
    
    async updateButtonState(button, groupName) {
        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    
        if (groupName === 'treatDevis') {
            const count = await this.detailServices.getDevisCount(demandeId);
            const devis = await this.detailServices.isOneDevisValidate(demandeId);
            if (count >= 3 || devis) {
                button.disabled = true;
                button.textContent = "Limite de devis atteinte ou déjà traité";
            }
        } else if (groupName === 'validateDevis') {
            const devis = await this.detailServices.isOneDevisValidate(demandeId);
            if (devis) {
                button.disabled = true;
                button.textContent = "Devis déjà validé";
            }
        } else if (groupName === 'treatBc') {
            const count = await this.detailServices.getBcCountFromDemandId(demandeId);
            if (count >= 1) {
                button.disabled = true;
                button.textContent = "BC déjà traité";
            }
        } else if (groupName === 'validateBc') {
            const bc = await this.detailServices.isOneBcValidate(demandeId);
            if (bc) {
                button.disabled = true;
                button.textContent = "BC déjà validé";
            }
        } else if (groupName === 'notifBcSend') {
            const [bcSend, invoiceValidating, invoiceValidated] = await Promise.all([
                this.detailServices.isOneNotifOnState(demandeId, 'bc_envoye_attente_livraison'),
                this.detailServices.isOneNotifOnState(demandeId, 'facture_a_valider'),
                this.detailServices.isOneNotifOnState(demandeId, 'commande_livree_finalisee')
            ]);
    
            if (bcSend || invoiceValidating || invoiceValidated) {
                button.disabled = true;
                button.textContent = "Notification déjà envoyée";
            }
        }
        const etat = await this.detailServices.getEtatDemande(demandeId);
        if (etat === 'commande_annulee') {
            button.disabled = true;
            button.textContent = "Commande annulée";
        }
    }
    
    

        // Fonction pour ouvrir la modale afin de déposer les devis
        async openUploadModal() {
            const demandeId = new URLSearchParams(window.location.search).get('demandeId');
            // Vérifiez le nombre actuel de devis
            const count = await this.detailServices.getDevisCount(demandeId);

            console.log('Nombre de devis actuel:', count);

            const modal = document.getElementById('uploadModal');
            const span = modal.querySelector('.close');
            
            modal.style.display = "block";
    
            span.onclick = function() {
                modal.style.display = "none";
            }
            
            window.onclick = function(event) {
                if (event.target == modal) {
                    modal.style.display = "none";
                }
            }
    
            document.getElementById('uploadForm').onsubmit = async function(e) {
    e.preventDefault();

    const formElement = document.getElementById('uploadForm');
    if (!formElement) {
        console.error('Le formulaire avec l\'ID "uploadForm" n\'a pas été trouvé.');
        return;
    }

    const formData = new FormData(formElement);
    const uploadSuccess = await this.detailServices.uploadFiles(demandeId,formData);

    if (uploadSuccess) {
        modal.style.display = "none";

        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
        const newType = "devis_a_valider"
        const newEtat = "devis_a_valider"
        await this.detailServices.updateNotificationType(demandeId, newType);
        await this.detailServices.updateDemandeEtat(demandeId, newEtat);
    }
}.bind(this);

        }

        // Fonction pour ouvrir la modale de validation des devis
async openValidateModal() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    const userId = new URLSearchParams(window.location.search).get('user');

    if (!demandeId) {
        console.error('ID de demande manquant dans l\'URL');
        return;
    }

    try {
        // Récupérer les devis pour la demande donnée
        const devis = await this.detailServices.getDevisPdfPath(demandeId);

        console.log('Devis récupérés:', devis);

        const modal = document.getElementById('validateModal');
        const span = modal.querySelector('.close');
        const closeButton = document.getElementById('closeValidateModal');
        const devisListDiv = document.getElementById('devisList');

        // Réinitialiser la liste des devis
        devisListDiv.innerHTML = '';

        // Créer un bouton pour chaque devis
        devis.forEach(pdfPath => {
            const devisDiv = document.createElement('div');
            devisDiv.className = 'devis-item';

            // Créer un bouton pour ouvrir le PDF
            const viewButton = document.createElement('button');
            viewButton.textContent = 'Voir le PDF';
            viewButton.className = 'btn btn-info';
            viewButton.onclick = () => window.open(pdfPath, '_blank'); // Ouvrir le PDF dans un nouvel onglet

            // Créer un bouton pour valider le devis
            const validateButton = document.createElement('button');
            validateButton.textContent = 'Valider';
            validateButton.className = 'btn btn-success';
            validateButton.type = 'submit';
            validateButton.onclick = async () => {
                // Utiliser l'ID du devis si nécessaire pour la validation
                const success = await this.detailServices.validateDevis(pdfPath);
                await this.detailServices.changeValideurNameThanksToUserId(userId,pdfPath);
                if (success) {
                    alert('Devis validé avec succès');
                    
                    const newType = "bc_a_editer"
                    const newEtat = "bc_a_editer"
                    await this.detailServices.updateNotificationType(demandeId, newType);
                    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                    // Vous pouvez ajouter ici du code pour rafraîchir la liste des devis si nécessaire
                    window.location.reload();
                } else {
                    alert('Erreur lors de la validation du devis');
                }
            };

            // Ajouter les boutons au div du devis
            devisDiv.appendChild(viewButton);
            devisDiv.appendChild(validateButton);

            // Ajouter le div au conteneur
            devisListDiv.appendChild(devisDiv);
        });

        // Ajouter le gestionnaire d'événement pour le bouton "Refuser"
        refuserButton.onclick = async () => {
            const confirmRefuser = confirm("Êtes-vous sûr de vouloir refuser la demande ?");
            if (confirmRefuser) {
                const newType = "commande_annulee";
                const newEtat = "commande_annulee";
                await this.detailServices.updateNotificationType(demandeId, newType);
                await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                alert('La demande a été annulée.');
                // Vous pouvez ajouter ici du code pour rafraîchir la liste des devis ou fermer le modal si nécessaire
                modal.style.display = 'none';
            }
        };

        // Ajouter les gestionnaires d'événements pour fermer la modal
        span.onclick = () => modal.style.display = 'none';
        closeButton.onclick = () => modal.style.display = 'none';

        // Afficher la modal
        modal.style.display = 'block';
    } catch (error) {
        console.error('Erreur lors de l\'ouverture du modal de validation:', error);
    }
}

async openBcUploadModal() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');

    if (!demandeId) {
        console.error('ID de demande manquant dans l\'URL');
        return;
    }

    const modal = document.getElementById('bcUploadModal');
    const span = modal.querySelector('.close');

    modal.style.display = "block";

    span.onclick = function() {
        modal.style.display = "none";
    };

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

    document.getElementById('bcUploadForm').onsubmit = async function(e) {
        e.preventDefault();

        const formElement = document.getElementById('bcUploadForm');
        if (!formElement) {
            console.error('Le formulaire avec l\'ID "bcUploadForm" n\'a pas été trouvé.');
            return;
        }

        const formData = new FormData(formElement);
        const uploadSuccess = await this.detailServices.uploadBcFiles(demandeId, formData);

        if (uploadSuccess) {
            modal.style.display = "none";

            // Mettre à jour le type de notification après le téléversement réussi
            const newType = "bc_a_valider";
            const newEtat = "bc_a_valider";
            await this.detailServices.updateNotificationType(demandeId, newType);
            await this.detailServices.updateDemandeEtat(demandeId, newEtat);
        }
    }.bind(this);
}

async openBcValidationModal() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    const userId = new URLSearchParams(window.location.search).get('user');

    if (!demandeId) {
        console.error('ID de demande manquant dans l\'URL');
        return;
    }

    try {
        // Récupérer les bons de commande pour la demande donnée
        const bcs = await this.detailServices.getBcPaths(demandeId);

        console.log('Bons de commande récupérés:', bcs);

        const modal = document.getElementById('validateBcModal');
        const span = modal.querySelector('.close');
        const bcListDiv = document.getElementById('bcList');
        const refuserButton = document.getElementById('refuserBcButton');

        // Réinitialiser la liste des bons de commande
        bcListDiv.innerHTML = '';

        // Créer un bouton pour chaque bon de commande
        bcs.forEach(pdfPath => {
            const bcDiv = document.createElement('div');
            bcDiv.className = 'bc-item';

            // Créer un bouton pour ouvrir le PDF
            const viewButton = document.createElement('button');
            viewButton.textContent = 'Voir le PDF';
            viewButton.className = 'btn btn-info';
            viewButton.onclick = () => window.open(pdfPath, '_blank'); // Ouvrir le PDF dans un nouvel onglet

            // Créer un bouton pour valider le bon de commande
            const validateButton = document.createElement('button');
            validateButton.textContent = 'Valider';
            validateButton.className = 'btn btn-success';
            validateButton.type = 'submit';
            validateButton.onclick = async () => {
                const success = await this.detailServices.validateBc(pdfPath);
                await this.detailServices.changeEditeurNameThanksToUserId(userId, pdfPath);
                if (success) {
                    const newType = "bc_valide_envoi_fournisseur";
                    const newEtat = "bc_valide_envoi_fournisseur";
                    await this.detailServices.updateNotificationType(demandeId, newType);
                    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                    alert('Bon de commande validé avec succès');
                    window.location.reload();
                    // Vous pouvez ajouter ici du code pour rafraîchir la liste des bons de commande si nécessaire
                } else {
                    alert('Erreur lors de la validation du bon de commande');
                }
            };

            // Ajouter les boutons au div du bon de commande
            bcDiv.appendChild(viewButton);
            bcDiv.appendChild(validateButton);

            // Ajouter le div au conteneur
            bcListDiv.appendChild(bcDiv);
        });

        // Ajouter le gestionnaire d'événement pour le bouton "Refuser"
        refuserButton.onclick = async () => {
            const confirmRefuser = confirm("Êtes-vous sûr de vouloir refuser la demande ?");
            if (confirmRefuser) {
                const newType = "commande_annulee";
                const newEtat = "commande_annulee";
                await this.detailServices.updateNotificationType(demandeId, newType);
                await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                alert('La demande a été annulée.');
                // Vous pouvez ajouter ici du code pour rafraîchir la liste des bons de commande ou fermer le modal si nécessaire
                modal.style.display = 'none';
            }
        };

        // Afficher le modal
        modal.style.display = 'block';

        // Ajouter les gestionnaires d'événements pour fermer le modal
        span.onclick = () => modal.style.display = 'none';
        window.onclick = (event) => {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };

    } catch (error) {
        console.error('Erreur lors de l\'ouverture du modal de validation:', error);
    }
}


async notifyBcSend() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    const newType = "bc_envoye_attente_livraison";
    const newEtat = "bc_envoye_attente_livraison";

    try {
        await this.detailServices.updateNotificationType(demandeId, newType);
        await this.detailServices.updateDemandeEtat(demandeId, newEtat);

        alert('Notification envoyée avec succès');

        // Recharge la page après l'envoi de la notification
        window.location.reload();
    } catch (error) {
        console.error('Erreur lors de l\'envoi de la notification:', error);
        alert('Une erreur est survenue lors de l\'envoi de la notification.');
    }
}


async openInvoiceUploadModal() {
    const modal = document.getElementById('invoiceUploadModal');
    const span = modal.querySelector('.close');
    
    modal.style.display = "block";
    
    span.onclick = function() {
        modal.style.display = "none";
    }
    
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    document.getElementById('invoiceUploadForm').onsubmit = async function(e) {
        e.preventDefault();
        
        const formElement = document.getElementById('invoiceUploadForm');
        if (!formElement) {
            console.error('Le formulaire avec l\'ID "invoiceUploadForm" n\'a pas été trouvé.');
            return;
        }

        const formData = new FormData(formElement);
        const demandeId = new URLSearchParams(window.location.search).get('demandeId');

        const uploadSuccess = await this.detailServices.uploadInvoiceFiles(demandeId, formData);
        
        if (uploadSuccess) {
            modal.style.display = "none";
            console.log('Facture téléversée avec succès');
            const newType = "facture_a_valider";
            await this.detailServices.updateNotificationType(demandeId, newType);
            
            // Optionnel : mettre à jour l'état ou notifier l'utilisateur
        }
    }.bind(this);
}

async openInvoiceValidationModal() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');
    const userId = new URLSearchParams(window.location.search).get('user');

    if (!demandeId) {
        console.error('ID de demande manquant dans l\'URL');
        return;
    }

    try {
        const factures = await this.detailServices.getInvoicePaths(demandeId);

        const modal = document.getElementById('invoiceValidationModal');
        const span = modal.querySelector('.close');
        const closeButton = document.getElementById('closeInvoiceValidationModal');
        const invoiceListDiv = document.getElementById('invoiceList');

        invoiceListDiv.innerHTML = '';

        factures.forEach(pdfPath => {
            const invoiceDiv = document.createElement('div');
            invoiceDiv.className = 'invoice-item';

            const viewButton = document.createElement('button');
            viewButton.textContent = 'Voir le PDF';
            viewButton.className = 'btn btn-info';
            viewButton.onclick = () => window.open(pdfPath, '_blank');

            const validateButton = document.createElement('button');
            validateButton.textContent = 'Valider';
            validateButton.className = 'btn btn-success';
            validateButton.type = 'submit';
            validateButton.onclick = async () => {
                const success = await this.detailServices.validateInvoice(pdfPath);
                await this.detailServices.changeSignataireNameThanksToUserId(userId,pdfPath);
                if (success) {
                    alert('Facture validée avec succès');
                    const newType = "commande_livree_finalisee";
                    const newEtat = "commande_livree_finalisee";
                    await this.detailServices.updateNotificationType(demandeId, newType);
                    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                    invoiceDiv.remove(); // Optionnel : retirer l'élément de la liste après validation
                } else {
                    alert('Erreur lors de la validation de la facture');
                }
            };

            invoiceDiv.appendChild(viewButton);
            invoiceDiv.appendChild(validateButton);

            invoiceListDiv.appendChild(invoiceDiv);
        });

        modal.style.display = 'block';

        span.onclick = function() {
            modal.style.display = 'none';
        };

        closeButton.onclick = function() {
            modal.style.display = 'none';
        };

        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };
    } catch (error) {
        console.error('Erreur lors de l\'ouverture du modal de validation de la facture:', error);
    }
}











        
    
    }


export { detailView };
