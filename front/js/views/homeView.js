import { homeServices } from "../../services/home-services.js";

class homeView{
    constructor(){
        this.userName = document.getElementById('user_nomprenom');
        this.userRole = document.getElementById('user_role');

        this.homeServices = new homeServices();
        this.updateUserNames();
        this.updateUserRole();

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

}



export {homeView};