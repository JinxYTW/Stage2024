import { homeServices } from "../../services/home-services.js";
import { utilisateurAuthentifie } from "../../services/auth.js"; 
class homeView{
    constructor() {
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');
        this.askSomethingButton = document.getElementById('ask_something');
        this.searchButton = document.getElementById('search_button');
        this.homeServices = new homeServices();

        

        this.updateUserNames();
        this.updateUserRole();
        this.bindAskSomething();
        this.bindSearchDemandes();
        this.generatePdfDemande();
    }

    

    bindSearchDemandes() {
        console.log('Binding search button');
        this.searchButton.addEventListener('click', async (event) => {
            console.log('Search button clicked');
            event.preventDefault(); // Empêche le rechargement de la page lors du clic sur le bouton de recherche
    
            const orderNumber = document.getElementById('order-number').value;
            console.log('Order number:', orderNumber);
            const orderDate = document.getElementById('order-date').value;
            console.log('Order date:', orderDate);
            const orderArticle = document.getElementById('order-article').value;
            console.log('Order article:', orderArticle);
            const orderDomain = document.getElementById('order-domain').value;
            console.log('Order domain:', orderDomain);
            const orderClient = document.getElementById('order-client').value;
            console.log('Order client:', orderClient);
    
            // Change le texte de l'élément avec l'ID "currentMonth"
            document.getElementById('currentMonth').textContent = 'Recherche Avancée';
    
            const demandes = await this.homeServices.searchDemandes(orderNumber, orderDate, orderArticle, orderDomain, orderClient);
            this.afficherDemandes(demandes);
        });
    }

    async afficherDemandes(demandes) {
        console.log('Demandes:', demandes);
    
        // Trier les demandes par date décroissante
        demandes.sort((a, b) => new Date(b.date_demande) - new Date(a.date_demande));
    
        const demandesContainer = document.getElementById('zone_commande'); // Assurez-vous d'avoir un conteneur avec cet ID
        demandesContainer.innerHTML = '';
    
        demandes.forEach(demande => {
            const demandeElement = document.createElement('div');
            demandeElement.className = 'order-item p-2 mb-2 bg-white rounded shadow-sm';
            demandeElement.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <p class="mb-1" id="numero_demande"><strong>COMMANDE SI${demande.demandeId}</strong></p>
                        <p class="mb-1" id="bind_project">${demande.projet_nom}</p>
                        <p class="mb-1" id="demandeur_nom_prenom">Commandée par ${demande.demandeur_nom_prenom}</p>
                    </div>
                    <div>
                        <button class="btn btn-outline-info btn-sm generate_pdf_demande" data-demande-id="${demande.demandeId}">Etat de la demande</button>
                        <p class="mb-1" id="etat_demande">${demande.etat}</p>
                    </div>
                    <div>
                        <button class="btn btn-outline-secondary btn-sm generate_pdf_devis" data-devis-id="${demande.demandeId}">Devis</button>
                        <button class="btn btn-outline-secondary btn-sm generate_pdf_bc" data-bc-id="${demande.demandeId}">BC</button>
                        <button class="btn btn-outline-secondary btn-sm generate_pdf_livraison" data-livraison-id="${demande.demandeId}">Livraison</button>
                        <p class="mb-1" id="date_demande">${new Date(demande.date_demande).toLocaleDateString()}</p>
                    </div>
                </div>
            `;
            demandesContainer.appendChild(demandeElement);
        });
    
        this.generatePdfDemande();
        this.generatePdfDevis();
        this.generatePdfBonCommande();
        this.generatePdfFacture();
        this.bindSearchDemandes();

    }

    bindAskSomething(){
        this.askSomethingButton.addEventListener('click',(event)=>{
            this.handleAskSomething();


    });
    }

    getDemandeIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('user');
    }

    async handleAskSomething() {
        const userId = this.getDemandeIdFromUrl();
        if (userId) {
            window.location.href = `ask.html?user=${userId}`;
        } else {
            alert('User ID not found in the URL.');
        }
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

    async generatePdfDemande(){
        document.querySelectorAll('.generate_pdf_demande').forEach(button => {
            button.addEventListener('click', async () => {

                
                const demandeId = button.getAttribute('data-demande-id');
                console.log('Demande ID:', demandeId);

                if (!demandeId) {
                    alert("L'identifiant de la demande est manquant.");
                    return;
                }

                // Vérifier si l'utilisateur est authentifié
                if (!utilisateurAuthentifie()) {
                    alert("Vous devez être authentifié pour accéder à cette ressource.");
                    return;
                }
                
                const pdfPath = await this.homeServices.generatePdfDemande(demandeId);
                console.log('Opening PDF at:', pdfPath);
                

                if (pdfPath) {
                    const adjustedPdfUrl = `http://127.0.0.1:8080/${pdfPath.replace('back/src/', '')}`;
                    console.log('Opening PDF at:', adjustedPdfUrl);
                
                    // Ouvrir le PDF dans une nouvelle fenêtre
                    const pdfTab = window.open(adjustedPdfUrl, '_blank');
                
                    if (pdfTab) {
                        console.log('PDF tab opened successfully');
                
                        // Ouvrir la page de détails dans une nouvelle fenêtre
                        const userId = new URLSearchParams(window.location.search).get('user');
                        const detailsTab = window.open(`detail.html?user=${userId}`, '_blank');
                
                        if (!detailsTab) {
                            alert("Le navigateur a bloqué l'ouverture de la nouvelle fenêtre pour les détails. Veuillez autoriser les pop-ups et réessayer.");
                        }
                    } else {
                        alert("Le navigateur a bloqué l'ouverture de la nouvelle fenêtre pour le PDF. Veuillez autoriser les pop-ups et réessayer.");
                    }
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

    }

    async generatePdfDevis(){
        document.querySelectorAll('.generate_pdf_devis').forEach(button => {
            button.addEventListener('click', async () => {
                const devisId = button.getAttribute('data-devis-id');
                console.log('Demande ID:', devisId);

                if (!devisId) {
                    alert("L'identifiant de la demande est manquant.");
                    return;
                }

                // Vérifier si l'utilisateur est authentifié
                if (!utilisateurAuthentifie()) {
                    alert("Vous devez être authentifié pour accéder à cette ressource.");
                    return;
                }
                
                const pdfPath = await this.homeServices.generatePdfDevis(devisId);
                console.log('Opening PDF at:', pdfPath);
                if (pdfPath) {
                    const adjustedPdfUrl = `http://127.0.0.1:8080/${pdfPath.replace('back/src/', '')}`;
                    console.log('Opening PDF at:', adjustedPdfUrl);

                    // Ouvrir le PDF dans une nouvelle fenêtre
                    window.open(adjustedPdfUrl, '_blank');
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

    }

    async generatePdfBonCommande(){
        document.querySelectorAll('.generate_pdf_bc').forEach(button => {
            button.addEventListener('click', async () => {
                const bcId = button.getAttribute('data-bc-id');
                console.log('Demande ID:', bcId);

                if (!bcId) {
                    alert("L'identifiant de la demande est manquant.");
                    return;
                }

                // Vérifier si l'utilisateur est authentifié
                if (!utilisateurAuthentifie()) {
                    alert("Vous devez être authentifié pour accéder à cette ressource.");
                    return;
                }
                
                const pdfPath = await this.homeServices.generatePdfBonCommande(bcId);
                console.log('Opening PDF at:', pdfPath);
                if (pdfPath) {
                    const adjustedPdfUrl = `http://127.0.0.1:8080/${pdfPath.replace('back/src/', '')}`;
                    console.log('Opening PDF at:', adjustedPdfUrl);

                    // Ouvrir le PDF dans une nouvelle fenêtre
                    window.open(adjustedPdfUrl, '_blank');
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

    }

    async generatePdfFacture(){
        document.querySelectorAll('.generate_pdf_livraison').forEach(button => {
            button.addEventListener('click', async () => {
                const livraisonId = button.getAttribute('data-livraison-id');
                console.log('Demande ID:', livraisonId);

                if (!livraisonId) {
                    alert("L'identifiant de la demande est manquant.");
                    return;
                }

                // Vérifier si l'utilisateur est authentifié
                if (!utilisateurAuthentifie()) {
                    alert("Vous devez être authentifié pour accéder à cette ressource.");
                    return;
                }
                
                const pdfPath = await this.homeServices.generatePdfFacture(livraisonId);
                console.log('Opening PDF at:', pdfPath);
                if (pdfPath) {
                    const adjustedPdfUrl = `http://127.0.0.1:8080/${pdfPath.replace('back/src/', '')}`;
                    console.log('Opening PDF at:', adjustedPdfUrl);

                    // Ouvrir le PDF dans une nouvelle fenêtre
                    window.open(adjustedPdfUrl, '_blank');
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

    }
    
    

    

}




export {homeView};