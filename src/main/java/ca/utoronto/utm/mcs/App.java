package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        Database database = new Database();

        server.createContext("/api/v1/addActor", new AddActor(database));
        server.createContext("/api/v1/addMovie", new AddMovie(database));
        server.createContext("/api/v1/addRelationship", new AddRelationship(database));
        server.createContext("/api/v1/getActor", new GetActor(database));
        server.createContext("/api/v1/getMovie", new GetMovie(database));
        server.createContext("/api/v1/hasRelationship", new HasRelationship(database));
        server.createContext("/api/v1/computeBaconNumber", new ComputeBaconNumber(database));
        server.createContext("/api/v1/computeBaconPath", new ComputeBaconPath(database));

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
}
