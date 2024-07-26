import java.io.File;

import controller.*; // Import all controllers
import webserver.WebServer;
import webserver.WebServerContext;
import webserver.WebServerSSEEventType;



public class App {
    
    /**
     * The main method is the entry point of the application.
     * It initializes the controllers and sets up the routes for handling HTTP requests.
     * It also starts the web server and listens on port 8080.
     *
     * @param args The command line arguments passed to the application.
     * @throws Exception If an error occurs during the execution of the main method.
     */

    public static void main(String[] args) throws Exception {
        //-------------- Controllers ----------------------------
        UtilisateurController myutilisateurController = new UtilisateurController();
        DemandeController mydemandeController = new DemandeController();
        BonCommandeController mybonCommandeController = new BonCommandeController();
        DevisController mydevisController = new DevisController();
        FactureController myfactureController = new FactureController();
        UtilisateurGroupeController myutilisateurGroupeController = new UtilisateurGroupeController();

        //-------------- Controllers non utilisé ----------------------------
        /* 

        NotifController mynotifController = new NotifController();
        RelanceController myrelanceController = new RelanceController();
        ProjetController myprojetController = new ProjetController();
        StockController mystockController = new StockController();
        FournisseurController myfournisseurController = new FournisseurController();
        
        */
        //-------------- Routes ----------------------------


        WebServer webserver = new WebServer();
        webserver.listen(8080);
        System.out.println("Server started on port 8080");

        //-------------- UtilisateurGroupe ----------------------------
        webserver.getRouter().post("/api/getGroupesNamesByUtilisateurId", (WebServerContext context) -> {
            System.out.println("Get Groupes Names By Utilisateur Id");
            myutilisateurGroupeController.getGroupesNamesByUtilisateurId(context);
        });

            //-------------- Utilisateur ----------------------------

        webserver.getRouter().post("/api/login",(WebServerContext context)->{
            
            myutilisateurController.login(context);
        });

        webserver.getRouter().get("/api/getNames/:id",(WebServerContext context)->{
            
            
            myutilisateurController.getNames(context);
        });
        
        webserver.getRouter().get("/api/getRole/:id", (WebServerContext context) -> {
            myutilisateurController.getRole(context);
        });

        webserver.getRouter().get("/api/validateToken", (WebServerContext context) -> {
            System.out.println("Validate Token");
            myutilisateurController.validateToken(context);
                
        });

            //-------------- Demande ----------------------------

        webserver.getRouter().get("/api/demande/details", (WebServerContext context) -> {
            mydemandeController.demandeDetails(context);
        });

        webserver.getRouter().get("/api/generatePdfDemande", (WebServerContext context) -> {
            
            mydemandeController.generatePdf(context);
        });

        webserver.getRouter().post("/api/ask", (WebServerContext context) -> {
            
            mydemandeController.createDemande(context);
        });

        webserver.getRouter().get("/api/searchOrders", (WebServerContext context) -> {
            System.out.println("Search Orders");
            mydemandeController.searchDemandes(context);
        });

        webserver.getRouter().get("/pdf/Demande/:filename", (WebServerContext context) -> {
            String fileName = context.getRequest().getParam("filename");
            String filePath = "back/src/pdf/Demande/" + fileName;
            
            // Lire le fichier et le renvoyer en réponse
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    context.getResponse().notFound("Fichier non trouvé");
                    return;
                }
                
                // Renvoyer le fichier en réponse
                context.getResponse().setContentType("application/pdf");
                context.getResponse().sendFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                context.getResponse().serverError("Erreur serveur lors de la récupération du fichier PDF");
            }
        });

            //-------------- Devis ----------------------------

        webserver.getRouter().get("/api/generatePdfDevis", (WebServerContext context) -> {
            
            mydevisController.generatePdf(context);
        });

        webserver.getRouter().get("/pdf/Devis/:filename", (WebServerContext context) -> {
            String fileName = context.getRequest().getParam("filename");
            String filePath = "back/src/pdf/Devis/" + fileName;
            
            // Lire le fichier et le renvoyer en réponse
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    context.getResponse().notFound("Fichier non trouvé");
                    return;
                }
                
                // Renvoyer le fichier en réponse
                context.getResponse().setContentType("application/pdf");
                context.getResponse().sendFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                context.getResponse().serverError("Erreur serveur lors de la récupération du fichier PDF");
            }
        });

        //-------------- BonCommande ----------------------------
        webserver.getRouter().get("/api/generatePdfBonCommande", (WebServerContext context) -> {
            
            mybonCommandeController.generatePdfBonCommande(context);
        });

        webserver.getRouter().get("/pdf/BonCommande/:filename", (WebServerContext context) -> {
            String fileName = context.getRequest().getParam("filename");
            String filePath = "back/src/pdf/BonCommande/" + fileName;
            
            // Lire le fichier et le renvoyer en réponse
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    context.getResponse().notFound("Fichier non trouvé");
                    return;
                }
                
                // Renvoyer le fichier en réponse
                context.getResponse().setContentType("application/pdf");
                context.getResponse().sendFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                context.getResponse().serverError("Erreur serveur lors de la récupération du fichier PDF");
            }
        });

            //-------------- Facture ----------------------------
        webserver.getRouter().get("/api/generatePdfFacture", (WebServerContext context) -> {
                
                myfactureController.generatePdf(context);
            });

        webserver.getRouter().get("/pdf/Facture/:filename", (WebServerContext context) -> {
            String fileName = context.getRequest().getParam("filename");
            String filePath = "back/src/pdf/Facture/" + fileName;
            
            // Lire le fichier et le renvoyer en réponse
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    context.getResponse().notFound("Fichier non trouvé");
                    return;
                }
                
                // Renvoyer le fichier en réponse
                context.getResponse().setContentType("application/pdf");
                context.getResponse().sendFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                context.getResponse().serverError("Erreur serveur lors de la récupération du fichier PDF");
            }
        });

            //-------------- Relance --------------------------------

        //-------------- SSE ----------------------------
        WebServer webServer = new WebServer();
        ConnectCallback connectCallback = new ConnectCallback();
        SubscribeCallback subscribeCallback = new SubscribeCallback();
        UnsubscribeCallback unsubscribeCallback = new UnsubscribeCallback();
        webServer.getSSE().addEventListeners(WebServerSSEEventType.CONNECT, connectCallback);
        webServer.getSSE().addEventListeners(WebServerSSEEventType.SUBSCRIBE, subscribeCallback);
        webServer.getSSE().addEventListeners(WebServerSSEEventType.UNSUBSCRIBE, unsubscribeCallback);

        webserver.getRouter().post("/api/demandes/utilisateur", (WebServerContext context) -> {
            
            mydemandeController.emitDemandesUtilisateur(context);
        });

        
        






        System.out.println("Hello, World!");
    }
}
