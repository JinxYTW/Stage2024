import { connectView } from "../views/connectView.js";

class connectController{
    constructor(){
        this.connectView = new connectView();
        this.connectView.bindLogin();
        
    }
}

export {connectController};