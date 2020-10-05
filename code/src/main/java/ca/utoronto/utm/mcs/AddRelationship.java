package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class AddRelationship implements HttpHandler {

    private Database database;

    public AddRelationship(Database database) {
        this.database = database;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRequestMethod().equals("PUT")) {
                handlePut(httpExchange);
            } else {
                httpExchange.sendResponseHeaders(400, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpExchange.sendResponseHeaders(500, -1);
        }
    }

    private void handlePut(HttpExchange httpExchange) throws IOException, JSONException {
        String body = Utils.convert(httpExchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        if (deserialized.has("actorId") && deserialized.has("movieId")) {
            String actorId = deserialized.getString("actorId");
            String movieId = deserialized.getString("movieId");

            httpExchange.sendResponseHeaders(database.linkMovieActor(actorId, movieId), -1);
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}