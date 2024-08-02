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
        NotifController mynotifController = new NotifController();

        //-------------- Controllers non utilisé ----------------------------
        /* 

        
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
            
            myutilisateurController.validateToken(context);
                
        });

            //-------------- Demande ----------------------------

        webserver.getRouter().get("/api/getEtatDemande", (WebServerContext context) -> {
            mydemandeController.getEtatDemande(context);
        });
        

            webserver.getRouter().post("/api/updateDemandeEtat", (WebServerContext context) -> {
                
                mydemandeController.updateDemandeEtat(context);
            });

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

            webserver.getRouter().post("/api/changeValideurNameThanksToUserId", (WebServerContext context) -> {
                
                mydevisController.changeValideurNameThanksToUserId(context);
            });

            webserver.getRouter().get("/api/isOneDevisValidate", (WebServerContext context) -> {
                
                mydevisController.isOneDevisValidate(context);
            });

            webserver.getRouter().post("/api/validateDevis", (WebServerContext context) -> {
                
                mydevisController.validateDevis(context);
            });

            webserver.getRouter().get("/api/getDevisPdfPath", (WebServerContext context) -> {
                
                mydevisController.getDevisPdfPath(context);
            });

            webserver.getRouter().get("/api/getDevisCount", (WebServerContext context) -> {
                
                mydevisController.getDevisCount(context);
            });

            webserver.getRouter().post("/uploadDevis", (WebServerContext context) -> {
                mydevisController.uploadDevis(context);
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

        webserver.getRouter().get("/pdf/Devis/:demandeId/:filename", (WebServerContext context) -> {
            String demandeId = context.getRequest().getParam("demandeId");
            String fileName = context.getRequest().getParam("filename");
            String filePath = "back/src/pdf/Devis/Demande" + demandeId + "/" + fileName;
            
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

        webserver.getRouter().post("/api/changeEditeurNameThanksToUserId", (WebServerContext context) -> {
                
            mybonCommandeController.changeEditeurNameThanksToUserId(context);
        });

        webserver.getRouter().get("/api/isOneBcValidate", (WebServerContext context) -> {
            
            mybonCommandeController.isOneBcValidate(context);
        });

        webserver.getRouter().get("/api/getBcPdfPath", (WebServerContext context) -> {
            
            mybonCommandeController.getBcPdfPathFromDemandId(context);
        });
        webserver.getRouter().post("/api/validateBc", (WebServerContext context) -> {
            
            mybonCommandeController.validateBc(context);
        });

        webserver.getRouter().get("/api/getBcCountFromDemandId", (WebServerContext context) -> {
            
            mybonCommandeController.getBcCountFromDemandId(context);
        });

        webserver.getRouter().post("/api/uploadBc", (WebServerContext context) -> {
            
            mybonCommandeController.uploadBc(context);
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

            webserver.getRouter().post("/api/changeSignataireNameThanksToUserId", (WebServerContext context) -> {
                
                myfactureController.changeSignataireNameThanksToUserId(context);
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

            

            //-------------- Notification --------------------------------
        
         webserver.getRouter().get("/api/isOneNotifOnState", (WebServerContext context) -> {
                
                mynotifController.isOneNotifOnState(context);
            });

        webserver.getRouter().get("/api/countNotifForUser", (WebServerContext context) -> {
                
                mynotifController.countNotifForUser(context);
            });

        webserver.getRouter().get("/api/getOldestUrgentNotification", (WebServerContext context) -> {
                
                mynotifController.getOldestUrgentNotification(context);
            });

        webserver.getRouter().get("/api/getNotificationsForUser", (WebServerContext context) -> {
                
                mynotifController.getNotificationsForUser(context);
            });

        webserver.getRouter().post("/api/markNotifAsRead", (WebServerContext context) -> {
                
                mynotifController.markAsRead(context);
            });
        
        webserver.getRouter().post("/api/updateNotificationTypeRead", (WebServerContext context) -> {
                
                mynotifController.updateNotificationTypeRead(context);
            });

            //Test

            webserver.getRouter().post("/api/markNotifAsReadForUSer", (WebServerContext context) -> {
                
                mynotifController.markAsReadForUser(context);
            });
        
        webserver.getRouter().post("/api/updateNotificationTypeRead", (WebServerContext context) -> {
                
                mynotifController.updateNotificationTypeReadForUser(context);
            });

            

        webserver.getRouter().post("/api/updateNotificationType", (WebServerContext context) -> {
                
                mynotifController.updateNotificationType(context);
            });

            

            //-------------- Relance --------------------------------

            //-------------- Facture --------------------------------

            webserver.getRouter().get("/api/isOneInvoiceValidate", (WebServerContext context) -> {
                
                myfactureController.isOneInvoiceValidate(context);
            });

            webserver.getRouter().get("/api/getInvoicePaths", (WebServerContext context) -> {
                
                myfactureController.getInvoicePathsFromDemandId(context);
            });

            webserver.getRouter().post("/api/validateInvoice", (WebServerContext context) -> {
                
                myfactureController.validateInvoice(context);
            });

            webserver.getRouter().get("/api/getFactureCountFromDemandId", (WebServerContext context) -> {
                
                myfactureController.getFactureCountFromDemandId(context);
            });

            webserver.getRouter().post("/api/uploadInvoice", (WebServerContext context) -> {
                
                myfactureController.uploadInvoice(context);
            });

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
