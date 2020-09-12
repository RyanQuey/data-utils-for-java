package com.ryanquey.datautils.helpers;

import java.lang.reflect.*;
import java.lang.IllegalAccessException;

public class DataClassesHelpers {
	// copies from objA to objB
	// https://stackoverflow.com/a/8132962/6952495
  static public void copyMatchingFields (Object objA, Object objB) throws Exception {
    Class objAClass = objA.getClass();
    Class objBClass = objB.getClass();
    Method[] methods = objAClass.getMethods();

    for (int i = 0; i < methods.length; i++) {
      String methodName = methods[i].getName();

      try {
        // don't want to try doing objA.setClass() ...
        if (methodName.startsWith("get") && methodName != "getClass"){
          Class returnType = methods[i].getReturnType();
          Method setter = objBClass.getMethod(methodName.replaceFirst("get", "set"), returnType);
          // System.out.println("calling " + methodName);
          Method getter = methods[i];

          // get from objA and then set it on objB
          setter.invoke(objB, getter.invoke(objA, null));
        }

      } catch (IllegalAccessException e) {
        throw e;
      } catch (InvocationTargetException e) {
        throw e;
      } catch (NoSuchMethodException e) {
        
          throw e;
      } catch (IllegalArgumentException e) {
        // TODO: handle exception...or do nothing probably
        throw e;
      }
    }
  }
}
