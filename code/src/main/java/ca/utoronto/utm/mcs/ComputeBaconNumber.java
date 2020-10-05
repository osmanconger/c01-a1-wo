package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class ComputeBaconNumber implements HttpHandler {

    private Database database;

    public ComputeBaconNumber(Database database) {
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

        if (deserialized.has("actorId")) {
            String actorId = deserialized.getString("actorId");
            String result = database.computeBaconNumber(actorId);
            if (!result.equals(404)) {
                JSONObject jsonObject = new JSONObject();
                String responseBody = jsonObject.put("baconNumber", result).toString();
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseBody.length());
                OutputStream outputStream = httpExchange.getResponseBody();
                try {
                    outputStream.write(responseBody.getBytes(Charset.defaultCharset()));
                } finally {
                    outputStream.close();
                }
            } else {
                httpExchange.sendResponseHeaders(404, -1);
            }
        } else {
            httpExchange.sendResponseHeaders(400, -1);
        }

    }
}
