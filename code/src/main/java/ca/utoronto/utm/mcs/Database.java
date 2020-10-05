package ca.utoronto.utm.mcs;

import static org.neo4j.driver.Values.parameters;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.DatabaseException;
import org.neo4j.driver.internal.InternalPath;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private Driver driver;
    private String uriDb;

    public Database() {
        uriDb = "bolt://localhost:7687";
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

    public boolean checkIfRelationShipExists(String actorId, String movieId) {
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                Result node_boolean = tx.run("RETURN EXISTS( (:Actor {actorId: $x})\n" +
                                "-[:ACTED_IN]-(:Movie {movieId: $y}) ) as bool"
                        ,parameters("x", actorId, "y", movieId) );
                if (node_boolean.hasNext()) {
                    return node_boolean.next().get("bool").toString().equals("TRUE");
                }
            }
        }
        return false;
    }

    public int checkIfHasRelationShip(String actorId, String movieId) {
        if(!(checkIfActorIdExists(actorId) && checkIfMovieIdExists(movieId))) {
            return 404;
        } else {
            return 200;
        }
    }

    public int linkMovieActor(String actorId, String movieId) {
        if(!(checkIfActorIdExists(actorId) && checkIfMovieIdExists(movieId))) {
            return 404;
        } else if (checkIfRelationShipExists(actorId, movieId)) {
            return 400;
        } else {
            try (Session session = driver.session()){
                session.writeTransaction(tx -> tx.run("MATCH (a:Actor {actorId:$x}),"
                        + "(t:Movie {movieId:$y})\n" +
                        "MERGE (a)-[r:ACTED_IN]->(t)\n" +
                        "RETURN r", parameters("x", actorId, "y", movieId)));
                session.close();
                return 200;
            }
        }
    }

    public String getActorName(String actorId) {
        if(!(checkIfActorIdExists(actorId))) {
            return "";
        } else {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()) {
                    Result actorName = tx.run("MATCH (a:Actor {actorId:$x})"
                            + "RETURN a.actorName", parameters("x", actorId));
                    String name = actorName.single().values().toString();
                    System.out.println(name);
                    return name;
                }
            }
        }
    }

    public String getMoviesActedIn(String actorId) {
        if(!(checkIfActorIdExists(actorId))) {
            return "";
        } else {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()) {
                    Result moviesActedIn = tx.run("MATCH (a:Actor {actorId:$x})-->(Movie)"
                            + "RETURN Movie.movieName", parameters("x", actorId));
                    List<Record> movies = new ArrayList<Record>();
                    if(moviesActedIn.hasNext()) {
                        movies = moviesActedIn.list();
                    }
                    System.out.println(movies.toString());
                    return movies.toString();

                }
            }
        }
    }

    public String getMovieName(String movieId) {
        if(!(checkIfMovieIdExists(movieId))) {
            return "";
        } else {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()) {
                    Result movieName = tx.run("MATCH (a:Movie {movieId:$x})"
                            + "RETURN a.movieName", parameters("x", movieId));
                    String name = movieName.single().values().toString();
                    System.out.println(name);
                    return name;

                }
            }
        }
    }

    public String getActorsActedIn(String movieId) {
        if(!(checkIfMovieIdExists(movieId))) {
            return "";
        } else {
            try (Session session = driver.session()){
                try (Transaction tx = session.beginTransaction()) {
                    Result actorsActedIn = tx.run("MATCH (a:Movie {movieId:$x})<--(Actor)"
                            + "RETURN Actor.actorName", parameters("x", movieId));
                    List<Record> actors = new ArrayList<Record>();
                    if(actorsActedIn.hasNext()) {
                        actors = actorsActedIn.list();
                    }
                    System.out.println(actors.toString());
                    return actors.toString();

                }
            }
        }
    }

    public String computeBaconNumber(String actorId) {
        try (Session session = driver.session())
        {
            if(getActorName(actorId).equals("Kevin Bacon")) {
                return "0";
            }
            try (Transaction tx = session.beginTransaction()) {
                Result node_boolean = tx.run("MATCH (k:Actor { actorName: 'Kevin Bacon' }),(m:Actor { actorId: $x }), p = shortestPath((k)-[:ACTED*]-(m))\n" +
                                "RETURN length(p) as length"
                        ,parameters("x", actorId) );
                if (node_boolean.hasNext()) {
                    try {
                        String code = node_boolean.next().get("length").toString();
                        System.out.println("code:  " + code);
                        return code;
                    } catch (Exception e) {
                        System.out.println("WIZERROR: " + e);
                        System.out.println("0");
                        return "0";
                    }
                } else {
                    // no paths exist
                    return "404";
                }

            }
        }
    }

    public void close() {
        driver.close();
    }

}
