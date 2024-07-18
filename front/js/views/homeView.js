import { homeServices } from "../../services/home-services.js";
import { utilisateurAuthentifie } from "../../services/auth.js"; 
class homeView{
    constructor() {
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');
        this.askSomethingButton = document.getElementById('ask_something');
        this.homeServices = new homeServices();

        

        this.updateUserNames();
        this.updateUserRole();
        this.bindAskSomething();
        this.generatePdfDemande();
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
                    window.open(adjustedPdfUrl, '_blank');
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

    }

    

}



export {homeView};