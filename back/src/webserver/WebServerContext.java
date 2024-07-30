package webserver;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

public class WebServerContext {
    private WebServerRequest request;
    private WebServerResponse response;
    private WebServerSSE sse;
    private Gson gson;

    WebServerContext(HttpExchange exchange, WebServerSSE sse)
    {
        this.request = new WebServerRequest(exchange);
        this.response = new WebServerResponse(exchange);
        this.sse = sse;
        this.gson = new Gson(); // Initialisez Gson ici
    }

    public WebServerRequest getRequest() {
        return request;
    }

    public WebServerResponse getResponse() {
        return response;
    }

    public WebServerSSE getSSE() {
        return sse;
    }

    /**
     * Extraire le corps de la requête et le convertir en un objet de type spécifié.
     * 
     * @param type Type de l'objet à créer
     * @param <T> Type générique
     * @return L'objet désérialisé
     * @throws IOException Si une erreur d'entrée/sortie se produit
     */
    public <T> T extractBody(Class<T> type) throws IOException {
        String bodyAsString = readRequestBodyAsString();
        try {
            return gson.fromJson(bodyAsString, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new IOException("Erreur de désérialisation JSON", e);
        }
    }

    /**
     * Lire le corps de la requête HTTP en tant que chaîne.
     * 
     * @return Corps de la requête en tant que chaîne
     * @throws IOException Si une erreur d'entrée/sortie se produit
     */
    private String readRequestBodyAsString() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getExchange().getRequestBody(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
