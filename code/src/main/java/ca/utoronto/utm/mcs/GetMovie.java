package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

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

            String responseBody = deserialized.put("name", movieName).put("actors", actorsActedIn).toString();
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, responseBody.length());
            OutputStream outputStream = httpExchange.getResponseBody();
            try {
                outputStream.write(responseBody.getBytes(Charset.defaultCharset()));
            } finally {
                outputStream.close();
            }

            httpExchange.sendResponseHeaders(200, -1);
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
