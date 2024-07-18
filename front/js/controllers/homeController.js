import { homeView } from "../views/homeView.js";

class homeController{
    constructor(){
        this.homeView = new homeView();
        this.homeView.bindAskSomething();
        this.demandes = [];
    }

    test(){
        console.log("Ici")
    }

    setDemandes(demandes) {
        this.demandes = demandes;
    }

    filterDemandesByMonth(month, year) {
        return this.demandes.filter(demande => {
            const demandeDate = new Date(demande.date_demande);
            return demandeDate.getMonth() === month && demandeDate.getFullYear() === year;
        });
    }

    afficherDemandes(demandes) {
        console.log("Le putain d'SSE fonctionne");
        this.homeView.afficherDemandes(demandes);
    }
}

export {homeController};