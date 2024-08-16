package org.molgenis.emx2.datamodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.molgenis.emx2.SelectColumn.s;
import static org.molgenis.emx2.datamodels.DataCatalogueCohortStagingLoader.DATA_CATALOGUE;
import static org.molgenis.emx2.datamodels.DataCatalogueCohortStagingLoader.SHARED_STAGING;

import org.junit.jupiter.api.*;
import org.molgenis.emx2.Database;
import org.molgenis.emx2.Schema;
import org.molgenis.emx2.sql.TestDatabaseFactory;

@TestMethodOrder(MethodOrderer.MethodName.class)
@Tag("slow")
public class TestLoaders {
  public static final String COHORT_STAGING = "CohortStaging";
  public static final String NETWORK_STAGING = "NetworkStaging";
  public static final String DATA_CATALOGUE_FLAT = "CatalogueFlat";
  public static final String DATA_CATALOGUE_AGGREGATES = "AggregatesTest";
  public static final String FAIR_DATA_HUB_TEST = "FAIRDataHubTest";
  public static final String DIRECTORY_TEST = "DirectoryTest";
  public static final String DIRECTORY_STAGING = "DirectoryStaging";
  public static final String RD3_TEST = "RD3Test";
  public static final String JRC_CDE_TEST = "JRCCDETest";
  public static final String FAIR_GENOMES = "FAIRGenomesTest";
  public static final String DCAT = "DCATTest";
  public static final String FAIR_DATA_POINT = "FAIRDataPointTest";
  public static final String DCAT_BASIC = "DCATBasicTest";
  public static final String PROJECT_MANAGER = "ProjectManager";
  public static final String CATALOGUE_ONTOLOGIES = "CatalogueOntologies";
  public static final String DIRECTORY_ONTOLOGIES = "DirectoryOntologies";
  public static final String FLAT_COHORTS_STAGING = "CohortsStagingFlat";
  public static final String FLAT_UMCG_COHORTS_STAGING = "UMCGStagingFlat";
  public static final String FLAT_STUDIES_STAGING = "StudiesStagingFlat";
  public static final String FLAT_NETWORKS_STAGING = "NetworksStagingFlat";
  public static final String FLAT_RWE_STAGING = "RWEStagingFlat";

  static Database database;

  @BeforeAll
  public static void setup() {
    database = TestDatabaseFactory.getTestDatabase();
    // prevend previous dangling test results
    database.dropSchemaIfExists(COHORT_STAGING);
    database.dropSchemaIfExists(NETWORK_STAGING);
    database.dropSchemaIfExists(DATA_CATALOGUE);
    database.dropSchemaIfExists(DATA_CATALOGUE_FLAT);
    database.dropSchemaIfExists(DATA_CATALOGUE_AGGREGATES);
    database.dropSchemaIfExists(FAIR_DATA_HUB_TEST);
    database.dropSchemaIfExists(SHARED_STAGING);
    database.dropSchemaIfExists(CATALOGUE_ONTOLOGIES);
    database.dropSchemaIfExists(DIRECTORY_TEST);
    database.dropSchemaIfExists(DIRECTORY_STAGING);
    database.dropSchemaIfExists(DIRECTORY_ONTOLOGIES);
    database.dropSchemaIfExists(RD3_TEST);
    database.dropSchemaIfExists(JRC_CDE_TEST);
    database.dropSchemaIfExists(FAIR_GENOMES);
    database.dropSchemaIfExists(DCAT);
    database.dropSchemaIfExists(PROJECT_MANAGER);
    database.dropSchemaIfExists(FLAT_COHORTS_STAGING);
    database.dropSchemaIfExists(FLAT_UMCG_COHORTS_STAGING);
    database.dropSchemaIfExists(FLAT_STUDIES_STAGING);
    database.dropSchemaIfExists(FLAT_NETWORKS_STAGING);
    database.dropSchemaIfExists(FLAT_RWE_STAGING);
  }

  @Test
  public void test1FAIRDataHubLoader() {
    Schema fairDataHubSchema = database.createSchema(FAIR_DATA_HUB_TEST);
    AvailableDataModels.FAIR_DATA_HUB.install(fairDataHubSchema, true);
    assertEquals(71, fairDataHubSchema.getTableNames().size());
    String[] semantics = fairDataHubSchema.getTable("BiospecimenType").getMetadata().getSemantics();
    assertEquals("http://purl.obolibrary.org/obo/NCIT_C70699", semantics[0]);
    assertEquals("http://purl.obolibrary.org/obo/NCIT_C70713", semantics[1]);
  }

  @Test
  public void test2DataCatalogueLoader() {
    Schema dataCatalogue = database.createSchema(DATA_CATALOGUE);
    AvailableDataModels.DATA_CATALOGUE.install(dataCatalogue, true);
    assertEquals(33, dataCatalogue.getTableNames().size());

    // test composite pkey having refs that are linked via refLink
    dataCatalogue
        .getTable("Variables")
        .groupBy()
        .select(s("count"), s("resource", s("name")), s("dataset", s("name")))
        .retrieveJSON();
  }

  @Test
  public void test7DataCatalogueCohortStagingLoader() {
    Schema cohortStaging = database.createSchema(COHORT_STAGING);
    AvailableDataModels.DATA_CATALOGUE_COHORT_STAGING.install(cohortStaging, true);
    assertEquals(19, cohortStaging.getTableNames().size());
  }

  @Test
  public void test8DataCatalogueNetworkStagingLoader() {
    Schema networkStaging = database.createSchema(NETWORK_STAGING);
    AvailableDataModels.DATA_CATALOGUE_NETWORK_STAGING.install(networkStaging, true);
    assertEquals(16, networkStaging.getTableNames().size());
  }

  @Test
  public void test9DirectoryLoader() {
    Schema directory = database.createSchema(DIRECTORY_TEST);
    AvailableDataModels.BIOBANK_DIRECTORY.install(directory, true);
    assertEquals(10, directory.getTableNames().size());
  }

  @Test
  void test10RD3Loader() {
    Schema RD3Schema = database.createSchema(RD3_TEST);
    AvailableDataModels.RD3.install(RD3Schema, true);
    assertEquals(27, RD3Schema.getTableNames().size());
  }

  @Test
  void test11JRCCDELoader() {
    Schema JRCCDESchema = database.createSchema(JRC_CDE_TEST);
    AvailableDataModels.JRC_COMMON_DATA_ELEMENTS.install(JRCCDESchema, true);
    assertEquals(12, JRCCDESchema.getTableNames().size());
  }

  @Test
  void test12FAIRGenomesLoader() {
    Schema FAIRGenomesSchema = database.createSchema(FAIR_GENOMES);
    AvailableDataModels.FAIR_GENOMES.install(FAIRGenomesSchema, true);
    assertEquals(46, FAIRGenomesSchema.getTableNames().size());
  }

  @Test
  void test13ProjectManagerLoader() {
    Schema ProjectManagerSchema = database.createSchema(PROJECT_MANAGER);
    AvailableDataModels.PROJECTMANAGER.install(ProjectManagerSchema, true);
    assertEquals(5, ProjectManagerSchema.getTableNames().size());
  }

  @Test
  void test14DCATLoader() {
    Schema DCATSchema = database.createSchema(DCAT);
    AvailableDataModels.DCAT.install(DCATSchema, true);
    assertEquals(23, DCATSchema.getTableNames().size());
  }

  @Test
  void test15DirectoryStagingLoader() {
    Schema directoryStaging = database.createSchema(DIRECTORY_STAGING);
    AvailableDataModels.BIOBANK_DIRECTORY_STAGING.install(directoryStaging, false);
    assertEquals(6, directoryStaging.getTableNames().size());
  }

  @Test
  void test16DCATBasic() {
    Schema DCATSchema = database.createSchema(DCAT_BASIC);
    new ProfileLoader("_profiles/test-only/DCAT-basic.yaml").load(DCATSchema, true);
    assertEquals(9, DCATSchema.getTableNames().size());
  }

  @Test
  void test17FAIRDataPointLoader() {
    Schema FDPSchema = database.createSchema(FAIR_DATA_POINT);
    AvailableDataModels.FAIR_DATA_POINT.install(FDPSchema, true);
    assertEquals(25, FDPSchema.getTableNames().size());
  }

  @Test
  void test17DataCatalogueFlatLoader() {
    Schema datacatalogueflat = database.createSchema(DATA_CATALOGUE_FLAT);
    AvailableDataModels.DATA_CATALOGUE_FLAT.install(datacatalogueflat, false);
    assertEquals(22, datacatalogueflat.getTableNames().size());
  }

  @Test
  void test18CohortsStagingFlatLoader() {
    Schema cohortsstagingflat = database.createSchema(FLAT_COHORTS_STAGING);
    AvailableDataModels.FLAT_COHORTS_STAGING.install(cohortsstagingflat, false);
    assertEquals(17, cohortsstagingflat.getTableNames().size());
  }

  @Test
  void test19UMCGStagingFlatLoader() {
    Schema umcgstagingflat = database.createSchema(FLAT_UMCG_COHORTS_STAGING);
    AvailableDataModels.FLAT_UMCG_COHORTS_STAGING.install(umcgstagingflat, false);
    assertEquals(10, umcgstagingflat.getTableNames().size());
  }

  @Test
  void test20StudiesFlatLoader() {
    Schema studiesstagingflat = database.createSchema(FLAT_STUDIES_STAGING);
    AvailableDataModels.FLAT_STUDIES_STAGING.install(studiesstagingflat, false);
    assertEquals(15, studiesstagingflat.getTableNames().size());
  }

  @Test
  void test21NetworksFlatLoader() {
    Schema networksstagingflat = database.createSchema(FLAT_NETWORKS_STAGING);
    AvailableDataModels.FLAT_NETWORKS_STAGING.install(networksstagingflat, false);
    assertEquals(17, networksstagingflat.getTableNames().size());
  }

  @Test
  void test22RWEStagingFlatLoader() {
    Schema rwestagingflat = database.createSchema(FLAT_RWE_STAGING);
    AvailableDataModels.FLAT_RWE_STAGING.install(rwestagingflat, false);
    assertEquals(16, rwestagingflat.getTableNames().size());
  }
}
