package com.ryanquey.datautils.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

// what model bases' inherit from, e.g., BookBase
// note that models themselves will get these methods too by inheritance
public class ModelBase {

  // good default toString helper for all models
  // https://stackoverflow.com/a/11669287/6952495
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
