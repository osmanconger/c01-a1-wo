package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import org.json.*;


public class AddActor implements HttpHandler {

    private Database database;

    public AddActor(Database database) {
        this.database = database;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRequestMethod().equals("PUT")) {
                handlePut(httpExchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpExchange.sendResponseHeaders(500, -1);
        }
    }

    private void handlePut(HttpExchange httpExchange) throws IOException, JSONException {
        String body = Utils.convert(httpExchange.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        if (deserialized.has("name") && deserialized.has("actorId")) {
            String actorName = deserialized.getString("name");
            String actorId = deserialized.getString("actorId");

            if(database.checkAndInsertActor(actorId, actorName)) {
                httpExchange.sendResponseHeaders(200, -1);
            } else {
                httpExchange.sendResponseHeaders(400, -1);
            }
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
