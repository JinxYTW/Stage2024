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

        webserver.getRouter().post("/api/login",(WebServerContext context)->{
            System.out.println("POST /api/login");
            myutilisateurController.login(context);
        });
        


        //-------------- SSE ----------------------------
        WebServer webServer = new WebServer();
        ConnectCallback connectCallback = new ConnectCallback();
        SubscribeCallback subscribeCallback = new SubscribeCallback();
        UnsubscribeCallback unsubscribeCallback = new UnsubscribeCallback();
        webServer.getSSE().addEventListeners(WebServerSSEEventType.CONNECT, connectCallback);
        webServer.getSSE().addEventListeners(WebServerSSEEventType.SUBSCRIBE, subscribeCallback);
        webServer.getSSE().addEventListeners(WebServerSSEEventType.UNSUBSCRIBE, unsubscribeCallback);






        System.out.println("Hello, World!");
    }
}
