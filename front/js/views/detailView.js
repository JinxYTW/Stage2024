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

   //----------------- Gère l'affichage des Notifications -----------------//
   getDemandeIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('user');
}

   async loadNotifications() {
    const userId = this.getDemandeIdFromUrl();
    if (userId) {
        try {
            const notificationCount = await this.homeServices.countNotifForUser(userId);
            const lastUrgentNotification = await this.homeServices.getOldestUrgentNotification(userId);

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
    if (notifId) {
        // Marquer la notification comme lue en utilisant la méthode dans homeServices
        const success = await this.homeServices.markNotifAsRead(notifId);
        if (success) {
            
            const updateSuccess = await this.homeServices.updateNotificationTypeRead(notifId);

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
        console.error('ID de notification manquant');
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
    
    async updateActionButtons() {
        const userId = new URLSearchParams(window.location.search).get('user');
        
        if (!userId) {
            console.error('ID utilisateur manquant dans l\'URL');
            return;
        }
    
        try {
            // Récupérer les groupes auxquels l'utilisateur appartient
            const groupes = await this.detailServices.getGroupesNames(userId);
    
            // La div où les boutons seront ajoutés
            const actionsButtonDiv = document.getElementById('actions_button');
    
            // Supprimer les boutons existants (si besoin)
            actionsButtonDiv.querySelectorAll('.dynamic-button').forEach(button => button.remove());
    
            // Mapping des groupes aux textes des boutons
            const buttonLabels = {
                
                'treatDevis': 'Traiter les devis',
                'validateDevis': 'Valider les devis',
                'treatBc': 'Traiter les BC',
                'validateBc': 'Valider les BC',
                'notifBcSend': 'Notifier l\'envoi des BC',
                'inventory': 'Gérer l\'inventaire'
            };
    
            // Mapping des groupes aux fonctions d'action
            const buttonActions = {
                
                'treatDevis': () => {
                    console.log('Action pour traiter les devis');
                    // Ajoutez ici la logique pour traiter les devis
                    
                },
                'validateDevis': () => {
                    console.log('Action pour valider les devis');
                    // Ajoutez ici la logique pour valider les devis
                    
                },
                'treatBc': () => {
                    console.log('Action pour traiter les BC');
                    // Ajoutez ici la logique pour traiter les BC
                },
                'validateBc': () => {
                    console.log('Action pour valider les BC');
                    // Ajoutez ici la logique pour valider les BC
                },
                'notifBcSend': () => {
                    console.log('Action pour notifier l\'envoi des BC');
                    // Ajoutez ici la logique pour notifier l'envoi des BC
                },
                'inventory': () => {
                    console.log('Action pour gérer l\'inventaire');
                    // Ajoutez ici la logique pour gérer l'inventaire
                }
            };
    
            // Filtrer les groupes pour ne conserver que ceux qui sont dans le mapping
        const validGroupes = groupes.filter(group => buttonLabels.hasOwnProperty(group));
        console.log('Groupes valides:', validGroupes);

        // Créer des boutons pour chaque groupe valide
        validGroupes.forEach(groupName => {
            // Créer un nouveau bouton
            const button = document.createElement('button');
            button.className = 'btn btn-info btn-block dynamic-button';
            button.textContent = buttonLabels[groupName] || `Action pour ${groupName}`; // Texte par défaut si le groupe n'est pas dans le mapping

            // Ajouter un attribut data-id pour identifier le groupe
            button.setAttribute('data-id', groupName);

            // Ajouter un gestionnaire d'événements avec l'action spécifique
            button.addEventListener('click', () => {
                if (buttonActions[groupName]) {
                    buttonActions[groupName](); // Appel de la fonction d'action
                } else {
                    console.log(`Aucune action définie pour le groupe : ${groupName}`);
                }
            });

            // Ajouter le bouton à la div
            actionsButtonDiv.appendChild(button);
        });

    
        } catch (error) {
            console.error('Erreur lors de la récupération des groupes:', error);
        }
    }
    
}

export { detailView };
