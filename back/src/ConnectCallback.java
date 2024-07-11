import webserver.WebServerSSEEvent;
import webserver.WebServerSSEEventHandler;

public class ConnectCallback implements WebServerSSEEventHandler{

    @Override
    public void run(WebServerSSEEvent event) {
        System.out.println("Connect: " + event.clientId());
    }
    
}
