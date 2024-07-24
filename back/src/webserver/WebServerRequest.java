package webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class WebServerRequest {
    private HttpExchange exchange;
    private final HashMap<String, String> params;
    private Object body;

    WebServerRequest(HttpExchange exchange) {
        this.exchange = exchange;
        this.params = new HashMap<>();
        this.body = null;
    }

    public String getMethod() {
        return this.exchange.getRequestMethod();
    }
    
    public String getPath() {
        return this.exchange.getRequestURI().getPath();
    }

    //Nouvelle méthode qui récupère les paramètres de la requête lorsqu'il y a présence de "?" dans l'URL
    public Map<String, String> getQueryParams() {
        Map<String, String> queryParams = new HashMap<>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getQuery();
        
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
                    String key = idx > 0 ? pair.substring(0, idx) : pair;
                    String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
                    queryParams.put(key, value);
                } catch (Exception e) {
                    // Log or handle any parsing exception
                }
            }
        }
        
        return queryParams;
    }


    public void setParams(HashMap<String, String> params) {
        this.params.clear();
        this.params.putAll(params);
    }

    public String getParam(String key) {
        return this.params.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T extractBody(Class<T> type) {
        if(body == null)
        {
            Headers headers = exchange.getRequestHeaders();
            String contentType = headers.getFirst("Content-Type");

            if (contentType.equals("application/json")) 
            {
                String bodyAsString = this.readStreamAsString();

                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();

                this.body = gson.fromJson(bodyAsString, type);
            }
        }

        return (T)this.body;
    }

    public String getBodyAsString() {
        return readStreamAsString();
    }

    private String readStreamAsString()
    {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);

            int character;
            StringBuilder buffer = new StringBuilder(512);
            while ((character = bufferReader.read()) != -1) {
                buffer.append((char) character);
            }

            bufferReader.close();
            inputStreamReader.close();

            return buffer.toString();
        }
        catch(Exception e)
        {
        
        }

        return "";
    }
    // Nouvelle méthode pour obtenir les en-têtes de la requête
    public Headers getHeaders() {
        return this.exchange.getRequestHeaders();
    }
}
