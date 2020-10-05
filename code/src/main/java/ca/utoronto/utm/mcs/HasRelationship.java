package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class HasRelationship implements HttpHandler {

    private Database database;

    public HasRelationship(Database database) {
        this.database = database;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRequestMethod().equals("GET")) {
                handleGet(httpExchange);
            } else {
                httpExchange.sendResponseHeaders(400, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpExchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException, JSONException {
        String body = Utils.convert(httpExchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        if (deserialized.has("actorId") && deserialized.has("movieId")) {
            String actorId = deserialized.getString("actorId");
            String movieId = deserialized.getString("movieId");

            if(database.checkIfHasRelationShip(actorId, movieId) == 200) {
                String responseBody = deserialized.put("hasRelationship", database.checkIfRelationShipExists(actorId, movieId)).toString();
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseBody.length());
                OutputStream outputStream = httpExchange.getResponseBody();
                try {
                    outputStream.write(responseBody.getBytes(Charset.defaultCharset()));
                } finally {
                    outputStream.close();
                }
            } else {
                httpExchange.sendResponseHeaders(database.checkIfHasRelationShip(actorId, movieId), -1);
            }

        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }
    }
}
