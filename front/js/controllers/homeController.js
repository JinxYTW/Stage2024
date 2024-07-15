import { homeView } from "../views/homeView.js";

class homeController{
    constructor(){
        this.homeView = new homeView();
        this.homeView.bindAskSomething();
    }
}

export {homeController};