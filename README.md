# data utils

Some utils for Cassandra, Elasticsearch, and just java in general that I like.

For examples, see my java podcast tool since that is where much of this originated

## How to add to your app

### For play app for example:
1) clone into a `./lib` dir
2) add es indices into resources if you use es
3) add cassandra daos for every dao class you have 

  ```
  @DaoFactory 
  PodcastByLanguageDao podcastByLanguageDao(@DaoTable String table);
  ```
  Note that the above should be literally `table`, that's it, not any tablename or anything else.

4) repackage
  ```
  mvn package
  ```

5) Use the jar wherever you need to in your project
6) Put the jar in `./lib`
  See [here](https://www.playframework.com/documentation/2.8.x/sbtDependencies) for more. I guess that's where play looks for jars.

7) Config
- Especially if you are using graph, but maybe otherwise at all, be sure that somewhere in your classpath is an applicaiton.conf file. 
  * If you are using graph, make sure to designate the graph name
    - this file seems to be Required if using graph, optional if using just some basic cassandra features
    - https://docs.datastax.com/en/developer/java-driver/4.9/manual/core/configuration/
    - https://community.datastax.com/answers/4545/view.html

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
3) ALTER KEYSPACE commands are not currently handled, since we don't want multiple changes to happen every migration run. Do these yourself manually. Probably best to just maintain a file that shows what keyspace should just be, so users can just set it up instantly. My default is to just make first migration 0000CreateKeyspace.cql
4) Other rules may also be added...e.g., right now, not allowing comments in opening line, for easier parsing of the command

## Development
TODO Project specific stuff should be included in gitignore
- How to hide InventoryMapper stuff? `src/main/java/com/ryanquey/datautils/cassandraHelpers/InventoryMapper.java`

See example in my `intertextuality-graph` project

### Installing to local repo 
use ./scripts/install-data-utils-jar.sh

### Versioning

If you want to bump the version, you don't want to use env vars for that in the pom.xml itself, since that would mean we couldn't keep track of the version of this project independent of other projects that use it as submodule. Better to just:

1) update the pom.xml var `project-package.version`
2) In parent project that uses this, set an env var there, and reference the new version, so other projects can find the pom/jar file with the new version. 

In my projects, this looks like changing a script that changes all the env vars. I like to use $DATA_UTILS_VERSION

Note that this env vars stuff is only for the sake of the parent project; this repo does not use it at all (currently)
