package com.brotherlogic.simplestore.postgres;

import com.brotherlogic.simplestore.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PgMapper implements Mapper {

  PgRunner runner;

  public PgMapper(PgRunner runner) {
    this.runner = runner;
  }

  /**
   * Stores the properties in the table.
   *
   * @param tableName the table to store in
   * @param properties the properties of the object to be stored
   * @return true if object was stored, false otherwise
   */
  @Override
  public boolean store(String tableName, Map<String, Object> properties) {
    String toAdd = "INSERT INTO " + tableName + " ( ";

    boolean first = true;
    Object[] values = new Object[properties.size()];
    int arrPointer = 0;
    for (String key : properties.keySet()) {
      values[arrPointer++] = properties.get(key);
      if (first) {
        toAdd += key;
        first = false;
      } else {
        toAdd += ", " + key;
      }
    }
    toAdd += ") VALUES (";
    first = true;
    for (int i = 0; i < properties.size(); i++) {
      if (first) {
        toAdd += "?";
        first = false;
      } else {
        toAdd += ",?";
      }
    }

    toAdd += ")";

    return runner.insert(toAdd, values);
  }

  /**
   * Retrieves an object from the table.
   *
   * @param tableName the name of the table
   * @param properties the properties of the object
   * @param types the types of each object
   * @return The retrieved object, or null if no such object exists
   */
  @Override
  public Object retrieve(Class<?> cls, String tableName, Map<String, Object> properties,
      Map<String, Class<?>> types) {
    String toGet = "SELECT * FROM " + tableName;
    if (properties.size() > 0) {
      toGet += " WHERE ";

      Object[] filler = new Object[properties.size()];
      int fillerPointer = 0;
      boolean first = true;
      for (Entry<String, Object> entry : properties.entrySet()) {
        if (first) {
          toGet += entry.getKey().toLowerCase() + " = ?";
          filler[fillerPointer++] = entry.getValue();
          first = false;
        } else {
          toGet += " AND " + entry.getKey().toLowerCase() + " = ?";
          filler[fillerPointer++] = entry.getValue();
        }
      }

      Map<String, Object> props = runner.retrieveOne(toGet, filler, types);

      try {
        // Prep the object for return
        Object tmpObject = cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        for (Field field : cls.getDeclaredFields()) {
          field.setAccessible(true);
          if (!Modifier.isFinal(field.getModifiers())) {
            field.set(tmpObject, props.get(field.getName().toLowerCase()));
          }
        }
        return tmpObject;
      } catch (Exception e) {
        System.err.println("FAILED BUILDING " + props);
        e.printStackTrace();
      }
    }

    return null;
  }

  /**
   * Adds a column to the table.
   *
   * @param tableName the name of the table
   * @param columnName the name of the column
   * @param objClass the class to be stored
   */
  @Override
  public void addColumn(String tableName, String columnName, Class<?> objClass) {
    runner.checkAndCreateTable(tableName);

    String sql = "ALTER TABLE  " + tableName + " ADD COLUMN " + columnName + " "
        + runner.getColumnType(objClass);
    runner.insert(sql, new Object[0]);
  }

  @Override
  public Set<String> getProperties(String tableName) {
    return runner.getTableColumns(tableName);
  }
}
