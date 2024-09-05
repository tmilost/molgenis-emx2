package org.molgenis.emx2.fairdatapoint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.emx2.datamodels.DataModels.Regular.PET_STORE;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.molgenis.emx2.Database;
import org.molgenis.emx2.Schema;
import org.molgenis.emx2.sql.TestDatabaseFactory;
import spark.Request;

public class FAIRDataPointNoCatalogsTest {

  static Database database;
  static Schema nocatalogs;

  @BeforeAll
  public static void setup() {
    database = TestDatabaseFactory.getTestDatabase();
    nocatalogs = database.dropCreateSchema("nocatalogs");
    PET_STORE.getImportTask(nocatalogs, true).run();
  }

  @Test
  @Disabled("This test is disabled because does not fail/succeed consistently.")
  public void FDPNoCatalogs() throws Exception {
    Request request = mock(Request.class);
    when(request.url()).thenReturn("http://localhost:8080/api/fdp");
    FAIRDataPoint fairDataPoint = new FAIRDataPoint(request, nocatalogs);
    fairDataPoint.setVersion("setversionforjtest");
    String result = fairDataPoint.getResult();
    assertFalse(result.contains("fdp-o:metadataCatalog"));
    assertFalse(result.contains("ldp:DirectContainer"));
    assertFalse(result.contains("dcterms:title \"Catalogs\";"));
    assertFalse(result.contains("ldp:hasMemberRelation"));
    assertFalse(result.contains("ldp:membershipResource"));
    assertFalse(result.contains("ldp:contains"));
  }
}
