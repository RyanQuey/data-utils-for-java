package com.ryanquey.datautils.models;

// Considering using this to let model classes inherit from
public interface Model {

  /*
   * persist to all tables, unless otherwise specified
   */
  public void persist ();

}
