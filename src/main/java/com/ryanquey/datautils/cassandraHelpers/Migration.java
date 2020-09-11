package com.ryanquey.datautils.cassandraHelpers;

import java.lang.Exception;

class Migration {
  // CQL to run
  String cql;
  // TODO get more specific on types of errors
  Boolean breakOnError = true;

  public Migration(String cqlCommand) throws Exception {
    // get the cql we will run
    this.cql = cqlCommand;

		// make sure that only one command is in each migration by checking for semi-colons
    int index = cqlCommand.indexOf(";");
		int count = 0;
		while (index != -1) {
		  // there's another match, continue
			count++;
			cqlCommand = cqlCommand.substring(index + 1);
			index = cqlCommand.indexOf("is");
		}

    if (count != 1) {
      throw new Exception("One cqlCommand query is required, and only one. We found " + count);
    }


    // if starts with ALTER TABLE assume that it only needs to be ran once
    if (this.cql.toUpperCase().startsWith("//") || this.cql.toUpperCase().startsWith("/*")) {
      throw new Exception("Not allowing comments in first line commands currently, for easier string parsing");
    }

    if (this.cql.toUpperCase().startsWith("ALTER TABLE")) {
      this.breakOnError = false;
    } else if (this.cql.toUpperCase().startsWith("ALTER KEYSPACE")) {
      // be careful with these ones, since we don't want to change these all the time.
      // Add a conditional, checking if keyspace is the same...later
      // For now, just skip. We don't want there to be multiple ALTER KEYSPACES, how would you check the logic then?
      throw new Exception("Not allowing ALTER KEYSPACE commands currently, do that separately to avoid back and forth keyspace changes");

      // barring comments for now. Though later, could add it back in and just remove all comments, but that would take more work
      /*
      if (this.cql.contains("//")) {
        throw new Exception("Not allowing comments in ALTER KEYSPACE commands currently, for easier string parsing");
      }

      String output = CassandraDb.execute("DESC KEYSPACE intertextuality_graph ;");
      System.out.println("Current keyspace def:" + output);

      String strippedOutput = output
        .replace("\n", "")
        .replace(" ", "")
        .toUpperCase();
      String strippedCqlCommand = this.cql 
        .replace("\n", "")
        .replace(" ", "")
        .toUpperCase();

      if (strippedOutput.equals(strippedCqlCommand)) {
        // ...

      }
      */
    } else {
      System.out.println("DEBUG Starts with: " + this.cql.toUpperCase().substring(0, 15));
    }
  }

  public void run () {
    try {
      System.out.println("Running migration: " + this.cql);
      CassandraDb.execute(this.cql);

    } catch (Exception e) {
      if (this.breakOnError) {
        throw e;
      } else {
        e.printStackTrace();
        System.out.println("this migration type is not idempotent, and made to fail when repeated. Continuing...");
      }

    }
  }
}
