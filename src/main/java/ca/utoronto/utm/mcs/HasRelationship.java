package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


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

            //database.insertActorId("1");
            //database.insertActorName(actorName);
            //database.insertActor(actorId, actorName);
            //database.close();

            httpExchange.sendResponseHeaders(200, -1);
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
