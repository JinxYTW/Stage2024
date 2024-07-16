package webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class WebServerResponse {
    private HttpExchange exchange;

    public WebServerResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void ok(String message) {
        this.send(200, message);
    }

    public void notFound(String message) {
        this.send(404, message);
    }

    public void serverError(String message) {
        this.send(500, message);
    }

    public void json(Object object) {
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        this.send(200, gson.toJson(object));
    }

    public void status(int statusCode, String message) {
        this.send(statusCode, message);
    }

    public void send(int statusCode, String message) {
        this.initCors();

        byte[] bytes = message.getBytes();

        try {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {

        }
    }

    private void initCors() {
        Headers headers = this.exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Methods", "*");
    }

    public OutputStream openSSEStream() {

        try {
            this.initCors();
            
            Headers responseHeaders = this.exchange.getResponseHeaders();
            responseHeaders.add("Content-Type", "text/event-stream");
            responseHeaders.add("Connection", "keep-alive");
            responseHeaders.add("Transfer-Encoding", "chunked");
            responseHeaders.add("X-Powered-By", "Native Application Server");

            this.exchange.sendResponseHeaders(200, 0);
        }
        catch(Exception e)
        {

        }

        return this.exchange.getResponseBody();
    }
    public void setContentType(String contentType) {
        Headers headers = this.exchange.getResponseHeaders();
        headers.add("Content-Type", contentType);
    }

    public void sendFile(File file) {
    try {
        // Lire le fichier en tant que tableau de bytes
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fis.read(buffer);
        fis.close();

        // Envoyer la réponse avec le contenu du fichier
        OutputStream os = this.exchange.getResponseBody();
        this.exchange.sendResponseHeaders(200, file.length());
        os.write(buffer);
        os.close();
    } catch (IOException e) {
        e.printStackTrace();
        this.serverError("Erreur serveur lors de l'envoi du fichier");
    }
}
}
