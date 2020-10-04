package ca.utoronto.utm.mcs;

import static org.neo4j.driver.Values.parameters;

import org.neo4j.driver.*;

public class Database {
    private Driver driver;
    private String uriDb;

    public Database() {
        uriDb = "bolt://localhost:11005";
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","1234"));
    }

    public boolean checkAndInsertActor(String actorId, String actorName) {
        if(!checkIfActorIdExists(actorId)) {
            insertActor(actorId, actorName);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfActorIdExists(String actorId) {
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                Result node_boolean = tx.run("MATCH (j:Actor {actorId: $x})"
                                + "RETURN j"
                        ,parameters("x", actorId) );
                if (node_boolean.hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void insertActor(String actorId, String actorName) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("CREATE (n:Actor { actorId: $x, actorName: $y })"
                    , parameters("x", actorId, "y", actorName)));
            session.close();
        }
    }

    public boolean checkAndInsertMovie(String movieId, String movieName) {
        if(!checkIfMovieIdExists(movieId)) {
            insertMovie(movieId, movieName);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfMovieIdExists(String movieId) {
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                Result node_boolean = tx.run("MATCH (j:Movie {movieId: $x})"
                                + "RETURN j"
                        ,parameters("x", movieId) );
                if (node_boolean.hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void insertMovie(String movieId, String movieName) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("CREATE (n:Movie { movieId: $x, movieName: $y })"
                    , parameters("x", movieId, "y", movieName)));
            session.close();
        }
    }

    public void linkMovieActor(String actorId, String movieId) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MATCH (a:ActorId {actorId:$x}),"
                    + "(t:MovieId {movieId:$y})\n" +
                    "MERGE (a)-[r:ACTED]->(t)\n" +
                    "RETURN r", parameters("x", actorId, "y", movieId)));
            session.close();
        }
    }

    public void close() {
        driver.close();
    }

}
