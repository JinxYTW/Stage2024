import webserver.WebServerSSEEvent;
import webserver.WebServerSSEEventHandler;

public class UnsubscribeCallback implements WebServerSSEEventHandler {

    @Override
    public void run(WebServerSSEEvent event) {
        System.out.println("Unsubscribe: " + event.clientId());
    }

}
