package org.molgenis.emx2.web.json;

import org.molgenis.emx2.SchemaMetadata;
import org.molgenis.emx2.TableMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Schema {
  private List<Table> tables = new ArrayList<>();

  public Schema() {
    // for json serialization
  }

  public Schema(SchemaMetadata schema) {
    // deterministic order is important for all kinds of comparisons
    List<String> list = new ArrayList<>();
    list.addAll(schema.getTableNames());
    Collections.sort(list);
    for (String tableName : list) {
      tables.add(new Table(schema.getTableMetadata(tableName)));
    }
  }

  public SchemaMetadata getSchemaMetadata() {
    SchemaMetadata s = new SchemaMetadata();
    for (Table t : tables) {
      TableMetadata tm = s.createTable(t.getName());
      tm.setPrimaryKey(t.getPkey());
      for (String[] u : t.getUniques()) tm.addUnique(u);
      for (Column c : t.getColumns()) {
        tm.addColumn(c.getColumnMetadata(tm));
      }
    }
    return s;
  }

  public List<Table> getTables() {
    return tables;
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }
}
