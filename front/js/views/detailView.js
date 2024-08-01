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
                'inventory': 'Depot de la facture'
            };
    
            // Mapping des groupes aux fonctions d'action
            const buttonActions = {
                'treatDevis': () => {
                    console.log('Action pour traiter les devis');
                    this.openUploadModal();
                },
                'validateDevis': () => {
                    console.log('Action pour valider les devis');
                    this.openValidateModal();
                },
                'treatBc': () => {
                    console.log('Action pour traiter les BC');
                    this.openBcUploadModal();
                },
                'validateBc': () => {
                    console.log('Action pour valider les BC');
                    this.openBcValidationModal();
                },
                'notifBcSend': () => {
                    console.log('Action pour notifier l\'envoi des BC');
                    this.notifyBcSend();
                },
                'inventory': {
                    'button1': () => {
                        console.log('Action pour la gestion de la facture 1');
                        this.openInvoiceUploadModal();
                        // Ajoutez la logique pour la première action d'inventaire
                    },
                    'button2': () => {
                        console.log('Action pour la gestion de la facture 2');
                        this.openInvoiceValidationModal();
                        // Ajoutez la logique pour la seconde action d'inventaire
                    }
                }
            };
    
            // Filtrer les groupes pour ne conserver que ceux qui sont dans le mapping
            const validGroupes = groupes.filter(group => buttonLabels.hasOwnProperty(group));
            console.log('Groupes valides:', validGroupes);
    
            // Créer des boutons pour chaque groupe valide
            validGroupes.forEach(async groupName => {
                // Vérifier si le groupe est 'inventory'
                if (groupName === 'inventory') {
                    // Créer le premier bouton pour 'inventory'
                    const button1 = document.createElement('button');
                    button1.className = 'btn btn-info btn-block dynamic-button';
                    button1.textContent = 'Depot de la facture'; // Texte spécifique pour le premier bouton
                    button1.setAttribute('data-id', 'inventory-button1');
                    button1.addEventListener('click', () => {
                        if (buttonActions['inventory'] && buttonActions['inventory']['button1']) {
                            buttonActions['inventory']['button1'](); // Appel de la fonction d'action pour le premier bouton
                        }
                    });
                    actionsButtonDiv.appendChild(button1);

                    const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const count = await this.detailServices.getFactureCountFromDemandId(demandeId);
                        if (count >= 1) {
                            button1.disabled = true;
                            button1.textContent = "Facture déjà traité";
                        } else {
                            button1.disabled = false;
                            button1.textContent = "Depot de la facture";
                        }


    
                    // Créer le deuxième bouton pour 'inventory'
                    const button2 = document.createElement('button');
                    button2.className = 'btn btn-info btn-block dynamic-button';
                    button2.textContent = 'Validation de la facture'; // Texte spécifique pour le deuxième bouton
                    button2.setAttribute('data-id', 'inventory-button2');
                    button2.addEventListener('click', () => {
                        if (buttonActions['inventory'] && buttonActions['inventory']['button2']) {
                            buttonActions['inventory']['button2'](); // Appel de la fonction d'action pour le deuxième bouton
                        }
                    });
                    actionsButtonDiv.appendChild(button2);
                    
                        const count2 = await this.detailServices.isOneInvoiceValidate(demandeId);
                        console.log('Facture validé:', count2);
                        if (count2 >= 1) {
                            button2.disabled = true;
                            button2.textContent = "Facture déjà validé";
                        } else {
                            button2.disabled = false;
                            button2.textContent = "Validation de la facture";
                        }
    
                } else {
                    // Créer un bouton pour les autres groupes
                    const button = document.createElement('button');
                    button.className = 'btn btn-info btn-block dynamic-button';
                    button.textContent = buttonLabels[groupName] || `Action pour ${groupName}`; // Texte par défaut si le groupe n'est pas dans le mapping
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
    
                    // Désactiver les boutons selon les conditions spécifiques
                    if (groupName === 'treatDevis') {
                        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const count = await this.detailServices.getDevisCount(demandeId);
                        const devis = await this.detailServices.isOneDevisValidate(demandeId);
                        if (count >= 3 || devis) {
                            button.disabled = true;
                            button.textContent = "Limite de devis atteinte ou déjà traité";
                        } else {
                            button.disabled = false;
                            button.textContent = "Traiter les devis";
                        }
                    }
    
                    if (groupName === 'validateDevis') {
                        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const devis = await this.detailServices.isOneDevisValidate(demandeId);
                        if (devis) {
                            button.disabled = true;
                            button.textContent = "Devis déjà validé";
                        } else {
                            button.disabled = false;
                            button.textContent = "Valider les devis";
                        }
                    }
    
                    if (groupName === 'treatBc') {
                        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const count = await this.detailServices.getBcCountFromDemandId(demandeId);
                        if (count >= 1) {
                            button.disabled = true;
                            button.textContent = "BC déjà traité";
                        } else {
                            button.disabled = false;
                            button.textContent = "Traiter les BC";
                        }
                    }
    
                    if (groupName === 'validateBc') {
                        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const bc = await this.detailServices.isOneBcValidate(demandeId);
                        if (bc) {
                            button.disabled = true;
                            button.textContent = "BC déjà validé";
                        } else {
                            button.disabled = false;
                            button.textContent = "Valider les BC";
                        }
                    }

                    if (groupName === 'notifBcSend') {
                        const demandeId = new URLSearchParams(window.location.search).get('demandeId');
                        const bcSend = await this.detailServices.isOneNotifOnState(demandeId, 'bc_envoye_attente_livraison');
                        const invoiceValidating = await this.detailServices.isOneNotifOnState(demandeId, 'facture_a_valider');
                        const invoiceValidated = await this.detailServices.isOneNotifOnState(demandeId, 'commande_livree_finalisee');
                        console.log('BC envoyé:', bcSend);

                        if (bcSend || invoiceValidating || invoiceValidated) {
                            button.disabled = true;
                            button.textContent = "Notification déjà envoyée";
                        } else {
                            button.disabled = false;
                            button.textContent = "Notifier l'envoi des BC";
                        }

                    }


                }
            });
    
        } catch (error) {
            console.error('Erreur lors de la récupération des groupes:', error);
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
                if (success) {
                    alert('Devis validé avec succès');
                    
                    const newType = "bc_a_editer"
                    const newEtat = "bc_a_editer"
                    await this.detailServices.updateNotificationType(demandeId, newType);
                    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                    // Vous pouvez ajouter ici du code pour rafraîchir la liste des devis si nécessaire
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

async  openBcValidationModal() {
    const demandeId = new URLSearchParams(window.location.search).get('demandeId');

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
            validateButton.onclick = async () => {
                const success = await this.detailServices.validateBc(pdfPath);
                if (success) {
                    const newType = "bc_valide_envoi_fournisseur";
                    const newEtat = "bc_valide_envoi_fournisseur";
                    await this.detailServices.updateNotificationType(demandeId, newType);
                    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
                    alert('Bon de commande validé avec succès');
                    
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

        modal.style.display = 'block';

        span.onclick = function() {
            modal.style.display = 'none';
        };

        window.onclick = function(event) {
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
    await this.detailServices.updateNotificationType(demandeId, newType);
    await this.detailServices.updateDemandeEtat(demandeId, newEtat);
    alert('Notification envoyée avec succès');

    // Ajoutez ici la logique pour envoyer la notification


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
            validateButton.onclick = async () => {
                const success = await this.detailServices.validateInvoice(pdfPath);
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
