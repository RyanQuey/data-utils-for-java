# data utils

Some utils for Cassandra, Elasticsearch, and just java in general that I like

## How to add to your app

### For play app for example:
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

### Alternatively add this project as a submodule
```
cd <my-parent-project>
git submodule add https://github.com/RyanQuey/data-utils-for-java.git
```

Now it is a [git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules), and you can manage it separately in git but develop simultaneously.


Later when you clone the project, you can use:
```
git clone https://github.com/<github-name>/<my-parent-project>.git --recurse-submodules
```

Or if you already cloned it you will have to run:
```
git submodule init
git submodule update
```

## DB Migrations
1) each migration should be in plaintext, and contain only a single cql command (i.e., only use ; once).
  * Technically we could allow more than one command, but for now validation requires checking each command and one command per file (particularly, ALTER TABLE calls are the only ones that are allowed to fail).
2) files should have .cql extension. If they don't, we will not run that file.
## Development
TODO Project specific stuff should be included in gitignore
- How to hide InventoryMapper stuff? `src/main/java/com/ryanquey/datautils/cassandraHelpers/InventoryMapper.java`

See example in my `intertextuality-graph` project
