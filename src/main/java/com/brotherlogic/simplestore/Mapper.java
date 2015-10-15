package com.brotherlogic.simplestore;

import java.util.Map;
import java.util.Set;

public interface Mapper {

  boolean store(String tableName, Map<String, Object> properties);

  Object retrieve(Class<?> cls, String tableName, Map<String, Object> properties,
      Map<String, Class<?>> types);

  void addColumn(String tableName, String columnName, Class<?> objClass);

  Set<String> getProperties(String tableName);

}
