package com.ryanquey.datautils.cassandraHelpers;

import com.ryanquey.datautils.helpers.FileHelpers;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import com.google.common.reflect.ClassPath;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Comparator;

class MigrationRunner {
  /*
   * In general will probably use this as a main class, to run migrations when specifically called.
   * It might be okay to also just allow this to be ran every single time the app gets built and started (?)
   */

  // migrations package name
  static String pathToMigrations;
  static String keyspaceName;

  static List<Migration> migrations = new ArrayList();

  public static void loadAll () throws IOException, Exception {
    File[] migrationFiles = FileHelpers.getFilesFromDir(pathToMigrations);
    // sort by filename, so first migrations are ran first
    Comparator<File> comparator = Comparator.comparing(File::getName);
    Arrays.sort(migrationFiles, comparator);

    for (File file : migrationFiles) {
 	  	if (file.isFile()) {
        String filename = file.getName();
        System.out.println("Migration Filename: " + filename);

        // make sure to rule out e.g., swp files
        String endsWith = filename.substring(filename.length() - 4);
        if (!endsWith.equals(".cql")) {
          System.out.println("skipping file that does not end in .cql; ends with " + endsWith);
          continue;
        }

        String cql = Files.asCharSource(file, StandardCharsets.UTF_8).read();// throws IOException
        Migration migration = new Migration(cql); // throws Exception
        System.out.println("Added new migration:" + migration.cql);

        migrations.add(migration);
    	}
		}
  }

  public static void runAll () {
    try {
      for (Migration migration : migrations) {
        migration.run();
      }

    } catch (Exception e) {
      // throw it, we want this to break if they are incorrect
      throw e;
    } 
  }

  //////////////////////////////////////
  // for when called as main class
  //////////////////////////////////////
  private static void processArgs(String[] args) throws Exception {
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     
    }

    if (args.length < 2) {
      throw new Exception("Argument required: {keyspaceName} {path to migrations}");
    }
    // Object newObject = Class.forName(strFullyQualifiedClassName).newInstance();
    MigrationRunner.keyspaceName = args[0];
    MigrationRunner.pathToMigrations = args[1];
  }

  // not running from within java job anymore, use cli, since should only be ran manually
  public static void main (String[] args) throws Exception {
    try {
      processArgs(args);
      System.out.println("== SETTING UP DB FOR MIGRATIONS ==");
      // dont' call use keyspace, since otherwise our first migration cannot run on a new db!
      CassandraDb.useKeyspaceOnInit = false;
      CassandraDb.initialize(keyspaceName);
      System.out.println("== LOADING ALL MIGRATIONS ==");
      loadAll();
      System.out.println("== RUNNING ALL MIGRATIONS ==");
      runAll();

    } catch (Exception e) {
      System.out.println("Error in Main:");
		  e.printStackTrace();
		  throw e;
    } catch (Throwable e) {
      System.out.println("Throwable in Main:");
		  e.printStackTrace();
		  throw e;

    } finally {
      // NOTE this will look like it build successfully even if we errored out. 
      // TODO only do this if we did not catch and throw the error.
      // then find out what error code to use (ie, not 0) for errors and throw that for errors
      Runtime.getRuntime().exit(0);
    }
  }
}
