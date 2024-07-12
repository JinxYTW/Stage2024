import { connectController } from "./controllers/connectController.js";

document.addEventListener("DOMContentLoaded", () => {
    console.log('DOM entièrement chargé et analysé');
    new connectController();
});