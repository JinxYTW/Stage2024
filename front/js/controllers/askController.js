import { askView } from "../views/askView.js";

class askController {
    constructor() {
        this.askView = new askView();
        this.askView.initializeForm();

        
    }
}

export { askController };