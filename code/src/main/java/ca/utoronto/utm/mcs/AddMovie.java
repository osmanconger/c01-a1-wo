package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class AddMovie implements HttpHandler {

    private Database database;

    public AddMovie(Database database) {
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

        if (deserialized.has("name") && deserialized.has("movieId")) {
            String movieName = deserialized.getString("name");
            String movieId = deserialized.getString("movieId");

            if(database.checkAndInsertMovie(movieId, movieName)) {
                httpExchange.sendResponseHeaders(200, -1);
            } else {
                httpExchange.sendResponseHeaders(400, -1);
            }
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
