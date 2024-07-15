import { homeServices } from "../../services/home-services.js";

class homeView{
    constructor() {
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');
        this.askSomethingButton = document.getElementById('ask_something');
        this.homeServices = new homeServices();

        document.querySelectorAll('.generate_pdf').forEach(button => {
            button.addEventListener('click', async () => {
                const demandeId = button.getAttribute('data-demande-id');
                console.log('Demande ID:', demandeId);

                if (!demandeId) {
                    alert("L'identifiant de la demande est manquant.");
                    return;
                }
                
                //Problème ici
                const pdfPath = await this.homeServices.generatePdf(demandeId);
                if (pdfPath) {
                    window.open(pdfPath, '_blank');
                } else {
                    alert("Échec de la génération du PDF.");
                }
            });
        });

        this.updateUserNames();
        this.updateUserRole();
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

    async handleAskSomething(){
        //A completer
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

    addGeneratePdfListener() {
        document.getElementById('generate_pdf').addEventListener('click', async () => {
            const demandeId = new URLSearchParams(window.location.search).get('demandeId');
            if (demandeId) {
                const pdfPath = await this.homeServices.generatePdf(demandeId);
                if (pdfPath) {
                    this.pdfLink.href = pdfPath;
                    this.pdfLink.textContent = 'Télécharger le PDF';
                } else {
                    this.pdfLink.textContent = 'Erreur lors de la génération du PDF';
                }
            } else {
                console.error('ID de la demande manquant dans l\'URL');
            }
        });
    }

}



export {homeView};