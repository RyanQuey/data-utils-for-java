package com.ryanquey.datautils.models;

public interface Model {

  /*
   * persist to all tables, unless otherwise specified
   */
  public void persist () throws Exception;

}
