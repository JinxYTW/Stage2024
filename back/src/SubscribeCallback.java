import webserver.WebServerSSEEvent;
import webserver.WebServerSSEEventHandler;

public class SubscribeCallback implements WebServerSSEEventHandler {

    @Override
    public void run(WebServerSSEEvent event) {
        System.out.println("Subscribe: " + event.clientId());
    }

}
