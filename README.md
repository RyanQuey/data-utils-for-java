# data utils

Some utils for Cassandra, Elasticsearch, and just java in general that I like

## How to use

For play app for example:
1) clone into a `./lib` dir
2) add es indices into resources if you use es
3) add cassandra daos for every dao class you have 

  ```
  @DaoFactory 
  PodcastByLanguageDao podcastByLanguageDao(@DaoTable String table);
  ```

4) repackage
  ```
  mvn package
  ```

5) Use the jar wherever you need to in your project
6) Put the jar in `./lib`
  See [here](https://www.playframework.com/documentation/2.8.x/sbtDependencies) for more. I guess that's where play looks for jars.

### DB Migrations
1) each migration should be in plaintext, and contain only a single cql command (i.e., only use ; once).
  * Technically we could allow more than one command, but for now validation requires checking each command and one command per file (particularly, ALTER TABLE calls are the only ones that are allowed to fail).
2) files should have .cql extension. If they don't, we will not run that file.
## Development
TODO Project specific stuff should be included in gitignore
- How to hide InventoryMapper stuff? `src/main/java/com/ryanquey/datautils/cassandraHelpers/InventoryMapper.java`
