package com.ryanquey.datautils.cassandraHelpers;

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
    if (cqlCommand.toUpperCase().startsWith("ALTER TABLE")) {
      this.breakOnError = false;
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
