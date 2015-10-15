package com.brotherlogic.simplestore.postgres;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PgRunner {

  Connection con;

  /**
   * Constructor.
   *
   * @param url The database url to connect to.
   */
  public PgRunner(String url) {
    try {
      Class.forName("org.postgresql.Driver");
      URI dbUri = new URI(url);

      String username = dbUri.getUserInfo().split(":")[0];
      String password = dbUri.getUserInfo().split(":")[1];
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort()
          + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
      con = DriverManager.getConnection(dbUrl, username, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the columns in the table.
   *
   * @param tableName The name of the table
   * @return A set of the column names for this table
   */
  public Set<String> getTableColumns(String tableName) {
    Set<String> columns = new TreeSet<String>();
    try {
      Statement state = con.createStatement();
      String sql = "SELECT column_name, data_type, character_maximum_length " + "FROM "
          + "INFORMATION_SCHEMA.COLUMNS where table_name = '" + tableName + "'";
      ResultSet rs = state.executeQuery(sql);
      while (rs.next()) {
        columns.add(rs.getString(1));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return columns;
  }

  /**
   * Runs an insert command.
   *
   * @param command The command to run
   * @param values the values to add to the command
   *
   * @return true if successful, false otherwise
   */
  public boolean insert(String command, Object[] values) {
    try {
      System.out.println("RUNNING INSERT: " + command);
      PreparedStatement ps = getPreparedStatement(command, values);
      ps.executeUpdate();
      return true;
    } catch (Exception e) {
      System.err.println("BAD INSERT = " + command + " with " + Arrays.toString(values));
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Retrieves a single object from the DB.
   *
   * @param command The command to run
   * @param values The values to fill
   * @param colTypes The types of each column
   *
   * @return Object mapping
   */
  public Map<String, Object> retrieveOne(String command, Object[] values,
      Map<String, Class<?>> colTypes) {
    try {
      System.out.println("COMMAND = " + command + " with " + Arrays.toString(values));
      PreparedStatement ps = getPreparedStatement(command, values);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        System.out.println("Exploring " + colTypes);
        Map<String, Object> objProps = new TreeMap<String, Object>();
        for (String col : colTypes.keySet()) {
          if (colTypes.get(col).equals(String.class)) {
            objProps.put(col, rs.getString(col));
          } else if (colTypes.get(col).equals(Integer.class)) {
            objProps.put(col, rs.getInt(col));
          }
        }
        return objProps;
      } else {
        return null;
      }
    } catch (Exception e) {
      System.err.println("BAD COMMAND = " + command + " given " + Arrays.toString(values));
      e.printStackTrace();
    }

    return null;
  }

  protected void checkAndCreateTable(String tableName) {
    try {
      String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "()";
      PreparedStatement ps = getPreparedStatement(sql, new Object[0]);
      ps.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected String getColumnType(Class<?> objClass) {
    if (objClass.equals(String.class)) {
      return "text";
    } else if (objClass.equals(Integer.class)) {
      return "integer";
    }

    return null;
  }

  protected PreparedStatement getPreparedStatement(String command, Object[] values) {
    try {
      PreparedStatement ps = con.prepareStatement(command);
      for (int i = 0; i < values.length; i++) {
        if (values[i] instanceof String) {
          ps.setString(i + 1, (String) values[i]);
        } else if (values[i] instanceof Integer) {
          ps.setInt(i + 1, (Integer) values[i]);
        }
      }
      return ps;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
