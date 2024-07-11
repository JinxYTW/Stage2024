import { homeView } from "../views/homeView.js";

class homeController{
    constructor(){
        this.homeView = new homeView();
    }
}

export {homeController};