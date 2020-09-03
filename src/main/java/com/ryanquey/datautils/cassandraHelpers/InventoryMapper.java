package com.ryanquey.datautils.cassandraHelpers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
/*
 *
 * https://github.com/datastax/java-driver/tree/4.x/manual/mapper
 *
 * How to take advantage of this interface in your app:
 * - Extend it, then instantiate it (ONLY ONCE) and then put it on your CassandraDb class as a class var
 * - Then you will have all these daos easily accessible
 * - See my intertextuality graph project for an example
 *
 * How to use: 
 *  InventoryMapper inventoryMapper = InventoryMapper.builder(session).build();
 *  PodcastDao dao = inventoryMapper.podcastDao("podcast_analysis_tool", "podcasts_by_language");
 *  dao.findById("this-is-the-podcast-id");
 *  
 *  OR 
 *
 *  dao.save(new Product(UUID.randomUUID(), "Mechanical keyboard"));
 *
 *
 *
 * Note that "The mapper maintains an interface cache. Calling a factory method with the same arguments will yield the same DAO instance"
 *
 *
 *
 */

// TODO setup something like this to have good default methods for DAO https://github.com/datastax/java-driver/tree/4.x/manual/mapper/daos#inheritance
@Mapper
public interface InventoryMapper {
  // for setting alternate podcast tables
  // @DaoFactory
  // PodcastByLanguageDao podcastByLanguageDao(@DaoTable String table);

  // helper so can use the inventoryMapper more easily.
  // https://github.com/datastax/java-driver/tree/4.x/manual/mapper/mapper#mapper-builder
  static MapperBuilder<InventoryMapper> builder(final CqlSession session) {
    // InventoryMapperBuilder is a class generated by default by appending "Builder" to teh end of the interface name, see same link above
    return new InventoryMapperBuilder(session);
  }

}
