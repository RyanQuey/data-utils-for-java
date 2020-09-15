package com.ryanquey.datautils.models;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import java.util.UUID;

// https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/daos/#inheritance
//@Dao use this for child interfaces
public interface BaseDao<T> {
  // if T doesn't work for whatever reason, just use Record as type for these

  /** Simple selection by full primary key. */
  /** Can't really do since we don't know primary key...will have to find a workaround. */
  /** Can we just build a bunch of difference methods with different types (doesn't work, java driver checks each one for primary key)? Or use a generic T type? */
  // @Select
  // T findOne(Integer primaryKey1, String primaryKey2);

  /* works if you put in the dao itself, not working on the base dao though, consistently returns "java.lang.IllegalArgumentException: arg0 is not a variable in this bound statement" 
  @Select(customWhereClause = "solr_query = :solr_query")
  T findOneBySolr(String solr_query); 
  */

  /* failed attempts to use solr queries through the dao
  // @Select(customWhereClause = "solr_query = :solrQuery")
  // @Select(customWhereClause = ":solrQuery")

  // @Query(":query")
  // T findOneByQuery(String query); 
  */

  /**
   * Selection by partial primary key, this will return multiple rows.
   *
   * <p>Also, note that this queries a different table: DAOs are not limited to a single entity, the
   * return type of the method dictates what rows will be mapped to.
   */
	/*
  @Select
  PagingIterable<EpisodeByLanguage> getByLanguage(String language);
  @Select
  PagingIterable<EpisodeByPrimaryGenre> getByLanguage(String language, String primaryGenre);
	*/

  /**
   * Creating a video is a bit more complex: because of denormalization, it involves multiple
   * tables.
   *
   * <p>A query provider is a nice way to wrap all the queries in a single operation, and hide the
   * details from the DAO interface.
   */
  // not doing for now, only doing one table
  /*
  @QueryProvider(
      providerClass = CreateEpisodeQueryProvider.class,
      entityHelpers = {Episode.class, UserEpisode.class, LatestEpisode.class, EpisodeByTag.class})
  void create(Episode video);
  */
  @Insert
  void create(T record);

  /**
   * Update using a template: the template must have its full primary key set; beyond that, any
   * non-null field will be considered as a value to SET on the target row.
   *
   * Also does up-serts
   *
   * <p>Note that we specify the null saving strategy for emphasis, but this is the default.
   *
   * For more on how to do customWhereClause and other options, see here: https://github.com/datastax/java-driver/tree/4.x/manual/mapper/daos/update#parameters
   *
   * TODO maybe have to pass in primary_genre and feed_url also, and work those into the customWhereClause also
   */
  @Update
  void save(T record);

  // TODO find a way t ojust make this an alias of update
  // @Update(nullSavingStrategy = NullSavingStrategy.DO_NOT_SET)
  // void save(Episode episode);
}
