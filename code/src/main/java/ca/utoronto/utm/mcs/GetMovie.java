package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetMovie implements HttpHandler {

    private Database database;

    public GetMovie(Database database) {
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

        if (deserialized.has("movieId")) {
            String movieId = deserialized.getString("movieId");
            String movieName = database.getMovieName(movieId);
            String actorsActedIn = database.getActorsActedIn(movieId);

            httpExchange.sendResponseHeaders(200, -1);
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
