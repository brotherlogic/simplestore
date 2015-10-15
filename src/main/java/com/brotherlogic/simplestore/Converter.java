package com.brotherlogic.simplestore;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class Converter {

  /**
   * Converts an object to a property mapping.
   * 
   * @param obj The object to be converted
   * @return Map of Key name to Object property value
   */
  public Map<String, Object> getProperties(Object obj) throws IllegalAccessException {
    Class<?> clz = obj.getClass();
    return getValues(clz.getDeclaredFields(), obj);
  }

  protected Map<String, Object> getValues(Field[] fields, Object obj)
      throws IllegalAccessException {
    Map<String, Object> retMap = new TreeMap<String, Object>();

    for (Field f : fields) {
      f.setAccessible(true);
      retMap.put(f.getName().toLowerCase(), f.get(obj));
    }
    return retMap;
  }



  /**
   * Converts an object to a property mapping.
   * 
   * @param cls The class of object to be converted
   * @return Map of Key name to Object property value
   */
  public Map<String, Class<?>> getClasses(Class<?> cls) {
    Map<String, Class<?>> toRet = new TreeMap<String, Class<?>>();

    for (Field f : cls.getDeclaredFields()) {
      toRet.put(f.getName().toLowerCase(), f.getType());
    }

    return toRet;
  }
}


