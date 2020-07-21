package org.molgenis.emx2;

import org.jooq.DataType;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.molgenis.emx2.ColumnType.*;
import static org.molgenis.emx2.utils.TypeUtils.getArrayType;
import static org.molgenis.emx2.utils.TypeUtils.toJooqType;

public class Column {
  private TableMetadata table;
  private String columnName;
  private ColumnType columnType = STRING;

  // relationships
  private String refTable;
  private String mappedBy;

  // options
  private int key = 0;
  private boolean nullable = false;
  private String validationScript = null;

  // todo implement below
  private boolean readonly = false;
  private String description = null;
  private String defaultValue = null;
  private boolean indexed = false;
  private boolean cascadeDelete = false;

  public Column(Column column) {
    copy(column);
  }

  public Column(TableMetadata table, Column column) {
    this.table = table;
    // todo validate
    copy(column);
  }

  /* copy constructor to prevent changes on in progress data */
  private void copy(Column column) {
    columnName = column.columnName;
    columnType = column.columnType;
    nullable = column.nullable;
    key = column.key;
    readonly = column.readonly;
    description = column.description;
    defaultValue = column.defaultValue;
    indexed = column.indexed;
    refTable = column.refTable;
    mappedBy = column.mappedBy;
    validationScript = column.validationScript;
    description = column.description;
    cascadeDelete = column.cascadeDelete;
  }

  public static Column column(String name) {
    return new Column(name);
  }

  public static Column column(String name, ColumnType type) {
    return new Column(name).type(type);
  }

  public Column(String columnName) {
    //    if (!columnName.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
    //      throw new MolgenisException(
    //          "Invalid column name '" + columnName + "'",
    //          "Column must start with a letter or underscore, followed by letters, underscores or
    // numbers");
    //    }
    this.columnName = columnName;
  }

  public Column(TableMetadata table, String columnName) {
    this.table = table;
    this.columnName = columnName;
  }

  public TableMetadata getTable() {
    return table;
  }

  public String getName() {
    return columnName;
  }

  public ColumnType getColumnType() {
    return columnType;
  }

  public List<Reference> getRefColumns() {
    List<Reference> refColumns = new ArrayList<>();
    if (getRefTable() == null) return refColumns;
    List<Column> pkeys = getRefTable().getPrimaryKeyColumns();
    for (Column keyPart : pkeys) {
      // todo check for composite keyPart
      if (keyPart.isReference()) {
        for (Reference ref : keyPart.getRefColumns()) {
          refColumns.add(
              new Reference(
                  this.getName() + "-" + ref.getName(), ref.getTo(), ref.getColumnType()));
        }

      } else {
        ColumnType type = keyPart.getColumnType();
        // all but ref is array
        if (!REF.equals(getColumnType())) {
          type = getArrayType(type);
        }
        // create the ref
        Reference ref =
            new Reference(
                this.getName() + (pkeys.size() > 1 ? "-" + keyPart.getName() : ""),
                keyPart.getName(),
                type);

        refColumns.add(ref);
      }
    }

    return refColumns;
  }

  private SchemaMetadata getSchema() {
    return getTable().getSchema();
  }

  public String getRefTableName() {
    return this.refTable;
  }

  public TableMetadata getRefTable() {
    if (this.refTable != null && getTable() != null) {
      // self relation
      if (this.refTable.equals(getTable().getTableName())) {
        return getTable(); // this table
      }
      // other relation
      SchemaMetadata schema = getSchema();
      if (schema != null) {
        TableMetadata result = schema.getTableMetadata(this.refTable);
        if (result == null) {
          throw new MolgenisException(
              "Internal error",
              "Column.getRefTable failed for column '"
                  + getName()
                  + "' because refTable '"
                  + getRefTableName()
                  + "' does not exist in schema '"
                  + schema.getName()
                  + "'");
        }
        return result;
      }
    }
    return null;
  }

  public Column key(int key) {
    this.key = key;
    return this;
  }

  public int getKey() {
    return this.key;
  }

  public Boolean isReadonly() {
    return readonly;
  }

  public Column setReadonly(boolean readonly) {
    this.readonly = readonly;
    return this;
  }

  public boolean isNullable() {
    return nullable;
  }

  public Boolean isCascadeDelete() {
    return cascadeDelete;
  }

  public Column nullable(boolean nillable) {
    this.nullable = nillable;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Column setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getMappedBy() {
    return mappedBy;
  }

  public Column mappedBy(String columnName) {
    this.mappedBy = columnName;
    return this;
  }

  public Column index(boolean indexed) {
    this.indexed = indexed;
    return this;
  }

  public Column cascadeDelete(boolean cascadeDelete) {
    if (cascadeDelete && !REF.equals(this.columnType)) {
      throw new MolgenisException(
          "Set casecadeDelete=true failed", "Columnn " + getName() + " must be of type REF");
    }
    this.cascadeDelete = cascadeDelete;
    return this;
  }

  public Boolean isIndexed() {
    return indexed;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getName()).append(" ");
    if (isReference()) {
      builder
          .append("" + getColumnType().toString().toLowerCase() + "(")
          .append(refTable)
          .append(")");
    }
    if (getKey() > 0) {
      builder.append(" key" + getKey());
    }
    if (Boolean.TRUE.equals(isNullable())) builder.append(" nullable");
    return builder.toString();
  }

  public Column type(ColumnType type) {
    if (type == null) {
      throw new MolgenisException("Add column failed", "Type was null for column " + getName());
    }
    this.columnType = type;
    return this;
  }

  public Column refTable(String refTable) {
    this.refTable = refTable;
    return this;
  }

  public Column setTable(TableMetadata table) {
    this.table = table;
    return this;
  }

  public String getTableName() {
    if (this.table != null) return this.table.getTableName();
    return null;
  }

  public String getValidation() {
    return validationScript;
  }

  public Column validation(String validationScript) {
    this.validationScript = validationScript;
    return this;
  }

  public Column pkey() {
    return this.key(1);
  }

  public Column removeKey() {
    this.key = 0;
    return this;
  }

  public Column setName(String columnName) {
    this.columnName = columnName;
    return this;
  }

  public Column getMappedByColumn() {
    return getRefTable().getColumn(getMappedBy());
  }

  public DataType getJooqType() {
    return toJooqType(getColumnType());
  }

  public Field asJooqField() {
    return field(name(getName()), getJooqType());
  }

  public org.jooq.Table getJooqTable() {
    return getTable().getJooqTable();
  }

  public boolean isReference() {
    return REF.equals(getColumnType())
        || MREF.equals(getColumnType())
        || REF_ARRAY.equals(getColumnType())
        || REFBACK.equals(getColumnType());
  }

  public String getSchemaName() {
    return getTable().getSchemaName();
  }

  public List<String> getRefColumnNames() {
    List<String> result = new ArrayList<>();
    for (Reference r : getRefColumns()) {
      result.add(r.getTo());
    }
    return result;
  }
}
