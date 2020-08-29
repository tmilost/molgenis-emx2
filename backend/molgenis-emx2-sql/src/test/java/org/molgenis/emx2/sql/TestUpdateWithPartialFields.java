package org.molgenis.emx2.sql;

import org.junit.BeforeClass;
import org.junit.Test;
import org.molgenis.emx2.*;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.molgenis.emx2.Column.column;
import static org.molgenis.emx2.FilterBean.f;
import static org.molgenis.emx2.TableMetadata.table;

public class TestUpdateWithPartialFields {

  private static Database db;

  @BeforeClass
  public static void setUp() throws SQLException {
    db = TestDatabaseFactory.getTestDatabase();
  }

  @Test
  public void test() {
    Schema schema = db.dropCreateSchema(TestUpdateWithPartialFields.class.getSimpleName());

    Table test =
        schema.create(
            table("test")
                .add(column("keycol").pkey())
                .add(column("col1").nullable(true))
                .add(column("col2").nullable(true)));

    test.insert(new Row().set("keycol", "row1").set("col1", "val11").set("col2", "val21"));

    // now if we update then val2 should be kept
    test.update(new Row().set("keycol", "row1").set("col1", "val_other"));
    assertEquals("val_other", test.retrieveRows().get(0).getString("col1"));
    assertEquals("val21", test.retrieveRows().get(0).getString("col2"));

    // now if we add more cols in sec row
    test.insert(
        new Row().set("keycol", "row2").set("col1", "val12"),
        new Row().set("keycol", "row3").set("col1", "val13").set("col2", "val23"));
    assertEquals(
        null,
        test.where(f("keycol", Operator.EQUALS, "row2")).retrieveRows().get(0).getString("col2"));
    assertEquals(
        "val23",
        test.where(f("keycol", Operator.EQUALS, "row3")).retrieveRows().get(0).getString("col2"));

    // now if we add more cols in first row
    test.insert(
        new Row().set("keycol", "row4").set("col1", "val14").set("col2", "val24"),
        new Row().set("keycol", "row5").set("col1", "val15"));
    assertEquals(
        "val24",
        test.where(f("keycol", Operator.EQUALS, "row4")).retrieveRows().get(0).getString("col2"));
    assertEquals(
        null,
        test.where(f("keycol", Operator.EQUALS, "row5")).retrieveRows().get(0).getString("col2"));
  }
}
