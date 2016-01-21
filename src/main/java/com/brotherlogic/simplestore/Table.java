package com.brotherlogic.simplestore;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Table {
  private String name;
  private Mapper mapper;
  private Class<?> cls;

  /**
   * Constructor.
   *
   * @param tableName The name of the table
   * @param mapper The mapper to convert the table into storage
   * @param clz The Class this table represents
   */
  public Table(String tableName, Mapper mapper, Class<?> clz) {
    name = tableName;
    this.mapper = mapper;
    cls = clz;
  }

  public boolean store(Map<String, Object> objectProperties) {
    adjust(objectProperties);
    return mapper.store(name, objectProperties);
  }

  public Object retrieve(Class<?> cls, Map<String, Object> objectProperties) {
    return mapper.retrieve(cls, name, objectProperties, new Converter().getClasses(cls));
  }

  protected void adjust(Map<String, Object> properties) {
    Set<String> currentProperties = mapper.getProperties(name);
    Set<String> toAdd = new TreeSet<String>();

    for (String col : properties.keySet()) {
      if (!currentProperties.contains(col)) {
        toAdd.add(col);
      }
    }

    for (String colToAdd : toAdd) {
      mapper.addColumn(name, colToAdd, properties.get(colToAdd).getClass());
    }
  }
}

