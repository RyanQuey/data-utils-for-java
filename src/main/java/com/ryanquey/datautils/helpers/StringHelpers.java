package com.ryanquey.datautils.helpers;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.google.common.base.CaseFormat;


public class StringHelpers {
  public static String snakeToCamel(String str) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str); 
  }

  public static String snakeToUpperCamel(String str) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str); 
  }

  public static String camelToSnake(String str) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str); 
  }

  public static String upperCamelToSnake(String str) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str); 
  }
}

