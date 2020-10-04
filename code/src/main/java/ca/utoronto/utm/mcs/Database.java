package ca.utoronto.utm.mcs;

import static org.neo4j.driver.Values.parameters;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Database {
    private Driver driver;
    private String uriDb;

    public Database() {
        uriDb = "bolt://localhost:7687";
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","1234"));
        System.out.println("database constructor done");
    }

    public void insertActorId(String actorId) {
        System.out.println("test 1");
        /*try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (a:ActorId {actorId: $x})",
                    parameters("x", actorId)));
            session.close();
        }*/
    }

    public void insertActorName(String actorName) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (a:ActorName {actorName: $x})",
                    parameters("x", actorName)));
            session.close();
        }
    }

    public void insertActor(String actorId, String actorName) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MATCH (a:ActorId {actorId:$x}),"
                    + "(t:ActorName {actorName:$y})\n" +
                    "MERGE (a)-[r:WROTE]->(t)\n" +
                    "RETURN r", parameters("x", actorId, "y", actorName)));
            session.close();
        }
    }

    public void close() {
        driver.close();
    }

}
