import { describe, expect, test } from "vitest";
import constants from "../../constants.js";
import {
  filterVisibleColumns,
  getChapterStyle,
  getRowErrors,
  getSaveDisabledMessage,
  removeKeyColumns,
  splitColumnIdsByHeadings,
  isMissingValue,
  isRequired,
} from "./formUtils";
import type { ITableMetaData, IColumn } from "metadata-utils";
const { AUTO_ID, HEADING } = constants;

describe("getRowErrors", () => {
  test("it should return no errors for an autoId field", () => {
    const rowData = { autoId: "1337" };
    const metadata = {
      columns: [{ id: "autoId", columnType: AUTO_ID }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return no errors for a heading field", () => {
    const rowData = { heading: "1337" };
    const metadata = {
      columns: [{ id: "heading", columnType: HEADING }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should an error if a required field misses a value", () => {
    const rowData = { required: undefined };
    const metadata = {
      columns: [
        {
          id: "required",
          label: "required",
          columnType: "STRING",
          required: "true",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({ required: "required is required" });
  });

  test("it should an error if a numerical required field has an invalid value", () => {
    const rowData = { required: NaN };
    const metadata = {
      columns: [
        {
          id: "required",
          label: "required",
          columnType: "DECIMAL",
          required: "true",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({ required: "required is required" });
  });

  test("it should give an error if a field is conditionally required on another field", () => {
    const rowData = {
      status: null,
      quantity: 6,
    };
    const metadata = {
      columns: [
        {
          id: "status",
          label: "status",
          columnType: "STRING",
          required: "if(quantity>5) 'if quantity > 5 required'",
        },
        {
          id: "quantity",
          label: "quantity",
          columnType: "DECIMAL",
          required: "true",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({
      status: "if quantity > 5 required",
    });
  });

  test("it should return no error if a field is conditionally required on another field and provided", () => {
    const rowData = {
      status: "RECEIVED",
      quantity: 6,
    };
    const metadata = {
      columns: [
        {
          id: "status",
          label: "status",
          columnType: "STRING",
          required: "if(quantity>5) 'if quantity > 5 required'",
        },
        {
          id: "quantity",
          label: "quantity",
          columnType: "DECIMAL",
          required: "true",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return no error it has no value and isn't required", () => {
    const rowData = { empty: null };
    const metadata = {
      columns: [{ id: "empty", columnType: "STRING" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return no error for a valid email address", () => {
    const rowData = { email: "blaat@blabla.bla" };
    const metadata = {
      columns: [{ id: "email", columnType: "EMAIL" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return an error for an invalid email address", () => {
    const rowData = { email: "in@valid" };
    const metadata = {
      columns: [{ id: "email", columnType: "EMAIL" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({ email: "Invalid email address" });
  });

  test("it should return no error for a valid hyperlink", () => {
    const rowData = { hyperlink: "https://google.com" };
    const metadata = {
      columns: [{ id: "hyperlink", columnType: "HYPERLiNK" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  //FIXME: Hyperlink checking seems to accept anything
  // test("it should return an error for an invalid hyperlink", () => {
  //   const rowData = { hyperlink: "google" };
  //   const metadata = {
  //     columns: [{ id: "hyperlink", columnType: "HYPERLiNK" }],
  //   } as ITableMetaData;
  //   const result = getRowErrors(metadata, rowData);
  //   expect(result).to.deep.equal({ hyperlink: "Invalid hyperlink" });
  // });

  test("it should return no error for a valid email address array array", () => {
    const rowData = { email: ["blaat@blabla.bla", "bla2@blabla.bla"] };
    const metadata = {
      columns: [{ id: "email", columnType: "EMAIL_ARRAY" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return an error for an invalid email address array", () => {
    const rowData = { email: ["in@valid", "val@id.com"] };
    const metadata = {
      columns: [{ id: "email", columnType: "EMAIL_ARRAY" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({ email: "Invalid email address" });
  });

  test("it should return no error for a valid hyperlink array", () => {
    const rowData = {
      hyperlink: ["https://google.com", "https://molgenis.org"],
    };
    const metadata = {
      columns: [{ id: "hyperlink", columnType: "HYPERLiNK_ARRAY" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  //FIXME: Hyperlink checking seems to accept anything
  // test("it should return an error for an invalid hyperlink array ", () => {
  //   const rowData = { hyperlink: ["google"] };
  //   const metadata = {
  //     columns: [{ id: "hyperlink", columnType: "HYPERLiNK_ARRAY" }],
  //   } as ITableMetaData;
  //   const result = getRowErrors(metadata, rowData);
  //   expect(result).to.deep.equal({ hyperlink: "Invalid hyperlink" });
  // });

  test("it should return no error for a successful validation", () => {
    const rowData = { validation: 2 };
    const metadata = {
      columns: [
        {
          id: "validation",
          columnType: "DECIMAL",
          validation: "validation > 1",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });

  test("it should return an error for an invalid validation", () => {
    const rowData = { validation: 0 };
    const metadata = {
      columns: [
        {
          id: "validation",
          columnType: "DECIMAL",
          validation: "validation > 1",
        },
      ],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({
      validation: "Applying validation rule returned error: validation > 1",
    });
  });

  test("it should return no error for a valid input", () => {
    const rowData = { valid: "input " };
    const metadata = {
      columns: [{ id: "valid", columnType: "STRING" }],
    } as ITableMetaData;
    const result = getRowErrors(metadata, rowData);
    expect(result).to.deep.equal({});
  });
});

describe("removeKeyColumns", () => {
  test("it should return the data without the data of the key columns", () => {
    const metadata = {
      columns: [{ id: "key", key: 1 }, { id: "some" }],
    } as ITableMetaData;
    const rowData = { some: "Data", key: "primaryKey" };
    const result = removeKeyColumns(metadata, rowData);
    expect(result).toEqual({ some: "Data" });
  });
});

describe("filterVisibleColumns", () => {
  test("it should return the columns if no visisble columns are defined", () => {
    const columns = [{ id: "col1" }, { id: "col2" }] as IColumn[];
    const visibleColumns = undefined;
    const result = filterVisibleColumns(columns, visibleColumns);
    expect(result).to.deep.equal(columns);
  });

  test("it should return only the visible columns", () => {
    const columns = [{ id: "col1" }, { id: "col2" }] as IColumn[];
    const visibleColumns = ["col2"];
    const result = filterVisibleColumns(columns, visibleColumns);
    expect(result).to.deep.equal([{ id: "col2" }]);
  });
});

describe("splitColumnIdsByHeadings", () => {
  test("it should split all columns by the headings", () => {
    const columns = [
      { id: "heading1", columnType: HEADING },
      { id: "string1", columnType: "STRING" },
      { id: "heading2", columnType: HEADING },
      { id: "string2", columnType: "STRING" },
      { id: "string3", columnType: "STRING" },
    ] as IColumn[];
    const result = splitColumnIdsByHeadings(columns);
    const expectedResult = [
      ["heading1", "string1"],
      ["heading2", "string2", "string3"],
    ];
    expect(result).to.deep.equal(expectedResult);
  });
});

describe("getChapterStyle", () => {
  test("it should return red style for a chapter with errors", () => {
    const page = ["id1", "id2", "id3"];
    const errors = { id1: "some error", id2: undefined };
    const result = getChapterStyle(page, errors);
    expect(result).to.deep.equal({ color: "red" });
  });

  test("it should return an empty style for a chapter without errors", () => {
    const page = ["id1", "id2", "id3"];
    const errors = { id1: undefined, id2: undefined };
    const result = getChapterStyle(page, errors);
    expect(result).to.deep.equal({});
  });
});

describe("getSaveDisabledMessage", () => {
  test("it should return an empty string if saving is possible", () => {
    const rowErrors = {};
    const result = getSaveDisabledMessage(rowErrors);
    expect(result).to.equal("");
  });

  test("it should return string, citing how many errors there are if there are any", () => {
    const rowErrors = { id1: "some error", id2: "another error" };
    const result = getSaveDisabledMessage(rowErrors);
    expect(result).to.equal("There are 2 error(s) preventing saving");
  });
});

describe("isMissingValue", () => {
  test("should return true if variable is considered to be missing", () => {
    expect(isMissingValue(undefined)).toBe(true);
    expect(isMissingValue(null)).toBe(true);
    expect(isMissingValue("")).toBe(true);
  });

  test("should return false if variable is considered not to be missing", () => {
    expect(isMissingValue(0)).toBe(false);
    expect(isMissingValue(false)).toBe(false);
    expect(isMissingValue("field1")).toBe(false);
  });

  test("should handle (nested) arrays correctly", () => {
    expect(isMissingValue([0])).toBe(false);
    expect(isMissingValue([1, 2, 3])).toBe(false);
    expect(isMissingValue([null, "field1", ""])).toBe(true);
    expect(isMissingValue([["field1", "field2"], "field3"])).toBe(false);
    expect(isMissingValue([[undefined, "field1"], "field2"])).toBe(true);
  });
});

describe("isRequired", () => {
  test("should return true for boolean type true and true strings", () => {
    expect(isRequired(true)).toBe(true);
    expect(isRequired("true")).toBe(true);
    expect(isRequired("True")).toBe(true);
    expect(isRequired("TRUE")).toBe(true);
  });

  test("should return false for boolean type false and true strings", () => {
    expect(isRequired(false)).toBe(false);
    expect(isRequired("false")).toBe(false);
    expect(isRequired("False")).toBe(false);
    expect(isRequired("FALSE")).toBe(false);
  });

  test("should return false for strings with an expression", () => {
    expect(isRequired("someValue > 0")).toEqual(false);
  });
});

describe("isValidHyperLink", () => {
  test("may contain '(' and or ')'", () => {
    expect(
      constants.HYPERLINK_REGEX.test("https://example.com/test".toLowerCase())
    ).toBe(true);
    expect(
      constants.HYPERLINK_REGEX.test("https://example.com/(test".toLowerCase())
    ).toBe(true);
    expect(
      constants.HYPERLINK_REGEX.test("https://example.com/test)".toLowerCase())
    ).toBe(true);
    expect(
      constants.HYPERLINK_REGEX.test("https://example.com/(test)".toLowerCase())
    ).toBe(true);
  });
});
