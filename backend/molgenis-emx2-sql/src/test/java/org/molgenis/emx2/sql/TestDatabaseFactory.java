package org.molgenis.emx2.sql;

import org.molgenis.emx2.Database;

public class TestDatabaseFactory {
  private static Database db;

  public static synchronized Database getTestDatabase() {
    if (db == null) {
      db = new SqlDatabase(true);
    }
    return db;
  }
}
