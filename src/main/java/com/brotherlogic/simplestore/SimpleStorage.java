package com.brotherlogic.simplestore;

import java.util.Map;

/**
 * Base for SimpleStorage.
 */
public class SimpleStorage {

  private Mapper mapper;

  public SimpleStorage(Mapper mapper) {
    this.mapper = mapper;
  }

  public boolean store(Object obj) {
    return store(obj, new Converter());
  }

  protected boolean store(Object obj, Converter conv) {
    Table table = getTable(obj.getClass());
    try {
      return table.store(conv.getProperties(obj));
    } catch (IllegalAccessException e) {
      return false;
    }
  }

  public Object retrieve(Class<?> cls, Map<String, Object> props) {
    Table table = getTable(cls);
    return table.retrieve(cls, props);
  }

  protected Table getTable(Class<?> cls) {
    return new Table(cls.getName().replace(".", "_").toLowerCase(), mapper, cls);
  }
}
