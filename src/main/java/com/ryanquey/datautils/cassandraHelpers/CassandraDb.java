package com.ryanquey.datautils.cassandraHelpers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.data.CqlDuration;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
// for graph stuff 
// TODO maybe move all this to separate project, to avoid having to put all of these graph dependencies into every project I do
import static com.datastax.dse.driver.api.core.graph.DseGraph.g;
// GraphResultSet
import com.datastax.dse.driver.api.core.graph.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.*;
import org.apache.tinkerpop.gremlin.structure.*;
import com.datastax.dse.driver.api.core.graph.ScriptGraphStatement;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;


import java.time.Instant;
import java.time.LocalDateTime;
// import java.sql.Timestamp;

// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

/*
 *
 * TODO
 * - make singleton class...currently using as Guice eager loader though, so maybe do not need to specify anything here
 */ 
public class CassandraDb {
  static InetSocketAddress cassandraIP;
  
  // InventoryMapper is our interface, built off of C* java driver stuff
  // We set this field here so that whatever class initializes CassandraDb can set their inventoryMapper on CassandraDb to have a globally available instance of inventoryMapper to use wherever CassandraDb is imported. 
  // if project wants to have this convenience, they will have to set it themselves.
  public static Object inventoryMapper;
  public static CqlSession session;
  public static String keyspaceName;
  public static Boolean useKeyspaceOnInit = true;
  // not using "g" since we're also importing g from datastax
  public static GraphTraversalSource graph;
  private static Boolean initialized = false;
    
  /*
   * NOTE assumes migrations have already been ran
   */

  // if no boolean passed, assumes no graph
  public static void initialize (String keyspaceNameStr) throws Exception {
    CassandraDb.initialize(keyspaceNameStr, false);
  }

  public static void initialize (String keyspaceNameStr, Boolean useGraph) throws Exception {
    int failedAttempts = 0;
    int maxAttempts = 5;

    while (!initialized) {
      try {
        _initialize(keyspaceNameStr, useGraph);
        initialized = true;
        return;

      } catch (Exception e) {
        // TODO remove this try catch, we'll just catch later
        e.printStackTrace();
        failedAttempts ++;
        if (failedAttempts >= maxAttempts) {
          throw e;
        } else {
          System.out.println("failed on attempt " + failedAttempts + "...trying again");
          TimeUnit.SECONDS.sleep(3);
        }
      }
    }
  }

  private static void _initialize (String keyspaceNameStr, Boolean useGraph) throws Exception {
    String kafkaIPAndPortStr = System.getenv("KAFKA_URL") != null ? System.getenv("KAFKA_URL") : "localhost:9092";
    String cassandraIPStr = System.getenv("CASSANDRA_URL") != null ? System.getenv("CASSANDRA_URL") : "127.0.0.1"; 
    String datacenter = System.getenv("CASSANDRA_DATACENTER") != null ? System.getenv("CASSANDRA_DATACENTER") : "dc1"; 
    Integer cassPort = System.getenv("CASSANDRA_PORT") != null ? Integer.parseInt(System.getenv("CASSANDRA_PORT")) : 9042; 
    keyspaceName = keyspaceNameStr;

    System.out.println("    URLs:");
    System.out.println("        Cassandra IP: " + cassandraIPStr);
    System.out.println("        Cassandra Port: " + cassPort);
    System.out.println("        Cassandra local datacenter: " + datacenter);
    System.out.println("        Cassandra keyspace: " + keyspaceNameStr);
    // not using kafka here, but let's just debug all in one place for now
    // TODO move this to teh kafka code
    System.out.println("        kafka IP: " + kafkaIPAndPortStr);
    cassandraIP = new InetSocketAddress(cassandraIPStr, cassPort); 

    // TODO try to import ./application.conf and use that?
    System.out.println("    setting the session");

    CqlSessionBuilder builderStarter = CqlSession.builder()
      .addContactPoint(cassandraIP)
      .withLocalDatacenter(datacenter); // now required since we're setting contact points

    if (System.getenv("CASSANDRA_PASS") != null) {
      // add authentication
      String cassandraUser = System.getenv("CASSANDRA_USER");
      System.out.print("auth with plain auth user: " + cassandraUser);
      String cassandraPassword = System.getenv("CASSANDRA_PASSWORD");

      builderStarter = builderStarter.withAuthCredentials(cassandraUser, cassandraPassword);
    }

    if (useKeyspaceOnInit) {
      CqlIdentifier keyspace = CqlIdentifier.fromCql(keyspaceName);
      CassandraDb.session = builderStarter
        .withKeyspace(keyspace)
        .build();

      System.out.println("    setting the inventory mapper for DAO");

      // create keyspace if doesn't exist already, and initialize tables
      System.out.println("    running db migrations");
      // NOTE I think I don't need this anymore,, because we're setting keyspace above
      session.execute("USE " + keyspaceName + ";");

    } else {
      CassandraDb.session = builderStarter
        .build();

      System.out.println("    setting the inventory mapper for DAO");
    }
    // if graph enabled, initialize graph session also
    if (useGraph) {
      CassandraDb.graph = CassandraDb.getGraphTraversalSource();

      if (CassandraDb.graph == null) {
        // since usingGraph was requested, something went wrong
        throw new Exception("ERROR initializing GraphTraversalSource");
      } 
    }

  }

  // close session when not actively using...or just when everything is finished running?
  public static void closeSession () {
    session.close();
    //TODO find out if have to close GraphTraversalSource
    // something like: g.close()
  }

  public static ResultSet execute (String cql) {
    return session.execute(cql);
  }

  public static void getReleaseVersion () {
    ResultSet rs = session.execute("select release_version from system.local"); 
    Row row = rs.one();

    System.out.println("release version:");
    System.out.println(row.getString("release_version"));
  };

  ////////////////////////////////////
  // some helpers for building queries
  
  public static Term getTimestamp() {
    // TODO note that this doesn't work, even though it looks like it should 
    // see https://docs.datastax.com/en/developer/java-driver/4.6/manual/core/#cql-to-java-type-mapping
    //return TypeCodecs.TypeCodec(Instant.now()); 
    // return LocalDateTime.now();// .toDate(); 
    // from this com.datastax.oss.driver.api.querybuilder.QueryBuilder.
    // returns a Term
    return currentTimestamp();
    //return TypeCodecs.TypeCodec(Instant.now()); 
  }

  // used raw, so there's no checking by compiler, but let's give it a try
  public static String getTimestampCQL() {
    return "toTimestamp(now())";
  }

  // string that matches format of cassandra's timestamp (Cassandra allows optional T letter)
  // something like: "2020-05-24T22:10:29.748809"
  public static String getTimestampStr() {
    return LocalDateTime.now().toString();
  }

  // TODO move to separate time helpers file
  // convert string to instant (which Cassandra codec accepts to be sent to cql for columns of type TIMESTAMP)
  public static Instant stringToInstant(String str) {
    return Instant.parse(str);
    /*
    return LocalDateTime.parse(str, DateTimeFormatter.ofPattern( "hh:mm a, EEE M/d/uuuu").toInstant();
    */
  }

  // NOTE untested, and currently not in use I don't think
  // CqlDuration already has a constructor taking a string. But this takes format 00:00:00  (HH:MM:SS) or 00:00 (MM:SS) instead of 
  public static CqlDuration stringToCqlDuration(String str) {
    String[] split = str.split(":");
    String hours;
    String minutes;
    String seconds;

    if (split.length == 3) {
      hours = split[0];
      minutes = split[1];
      seconds = split[2];
    } else {
      // assume other format supported by Rome RSS, MM:SS
      hours = "00";
      minutes = split[0];
      seconds = split[1];
    }

    // p for period, T for time. Alternative ISO 8601 format
    return CqlDuration.from(String.format("PT%s:%s:%s", hours, minutes, seconds));
  }

  /*
   * https://docs.datastax.com/en/developer/java-driver/4.8/manual/core/dse/graph/
   * For "Building a traversal with the TinkerPop fluent API, and executing it explicitly with the session" (in contrast with implicit execution)
   *
   * Use doing something like this:
   * import static com.datastax.dse.driver.api.core.graph.DseGraph.g;
   * GraphTraversal<Vertex, Vertex> traversal = g.V().has("name", "marko");
   *
   * GraphResultSet result = CassandraDb.executeGraphTraversal(statement);
   * for (GraphNode node : result) {
   *   System.out.println(node.asVertex());
   * }
   *
   * Note that you can also just do g.V().next() for example, don't need to use executeGraphGraversal necessarily...I think
   */
  public static GraphResultSet executeGraphTraversal(GraphTraversal<Vertex, Vertex> traversal) {
    FluentGraphStatement statement = FluentGraphStatement.newInstance(traversal);
    GraphResultSet result = CassandraDb.session.execute(statement);

    return result;
  }

  public static GraphResultSet executeGraphString(String groovyString) {
    ScriptGraphStatement statement = ScriptGraphStatement.newInstance(groovyString);
    GraphResultSet result = CassandraDb.session.execute(statement);

    return result;
  }


  /*
   * For "Building a connected traversal with the fluent API, and executing it implicitly by invoking a terminal step"
   * https://docs.datastax.com/en/developer/java-driver/4.8/manual/core/dse/graph/
   *
   * Use doing something like this:
   * GraphTraversalSource g = CassandraDb.getGraphTraversalSource();
   * List<Vertex> vertices = g.V().has("name", "marko").toList();
   *
   * for an app, consider calling once on initialization and setting on the CassandraDb class as static field, so you don't start too many sessions
   * (can just pass in true for useGraph arg when calling CassandraDb.initialize and that will do it for you)
   *
   * CassandraDb.initialize("my-ks", true)
   * CassandraDb.graph.V()
   *
   * TODO The method withRemote(RemoteConnection) from the type GraphTraversalSource is deprecated [67108967]. However, it's what DS uses in their docs, so keeping for now
   */
  public static GraphTraversalSource getGraphTraversalSource() {
    System.out.println("Initializing graph traversal source");

    // GraphTraversalSource g = DseGraph.g
    // https://docs.datastax.com/en/developer/java-driver/4.9/manual/core/dse/graph/fluent/implicit/
    GraphTraversalSource graph = AnonymousTraversalSource.traversal()
          .withRemote(DseGraph.remoteConnectionBuilder(CassandraDb.session).build());

    return graph;
  }
}
