import java.io.File;

import controller.*; // Import all controllers
import models.Devis;
import webserver.WebServer;
import webserver.WebServerContext;
import webserver.WebServerResponse;
import webserver.WebServerSSEEventType;



public class App {
    public static void main(String[] args) throws Exception {
        //-------------- Controllers ----------------------------
        UtilisateurController myutilisateurController = new UtilisateurController();
        DemandeController mydemandeController = new DemandeController();
        BonCommandeController mybonCommandeController = new BonCommandeController();
        NotifController mynotifController = new NotifController();
        DevisController mydevisController = new DevisController();
        StockController mystockController = new StockController();
        FournisseurController myfournisseurController = new FournisseurController();
        FactureController myfactureController = new FactureController();
        RelanceController myrelanceController = new RelanceController();
        ProjetController myprojetController = new ProjetController();

        //-------------- Routes ----------------------------


        WebServer webserver = new WebServer();
        webserver.listen(8080);
        System.out.println("Server started on port 8080");

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

            //-------------- Demande ----------------------------

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
            System.out.println("Demandes utilisateur");
            mydemandeController.emitDemandesUtilisateur(context);
        });

        
        






        System.out.println("Hello, World!");
    }
}
