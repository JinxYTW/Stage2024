import { homeView } from "../views/homeView.js";

class homeController{
    constructor(){
        this.homeView = new homeView();
        this.homeView.bindAskSomething();
    }
    
    test(){
        console.log("Ici")
    }

    afficherDemandes(demandes) {
        console.log("ok")
        this.homeView.afficherDemandes(demandes);
    }
}

export {homeController};