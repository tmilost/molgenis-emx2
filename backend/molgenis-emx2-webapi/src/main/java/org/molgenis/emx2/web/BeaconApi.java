package org.molgenis.emx2.web;

import static org.molgenis.emx2.json.JsonUtil.getWriter;
import static org.molgenis.emx2.web.MolgenisWebservice.getSchemaNames;
import static spark.Spark.get;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.molgenis.emx2.Schema;
import org.molgenis.emx2.Table;
import org.molgenis.emx2.beacon.requests.BeaconRequestBody;
import org.molgenis.emx2.beacon.responses.BeaconFilteringTermsResponse;
import org.molgenis.emx2.beaconv2.responses.*;
import org.molgenis.emx2.beaconv2_prev.ServiceInfo;
import spark.Request;
import spark.Response;

// is a beacon on level of database, schema or table?
public class BeaconApi {

  private static MolgenisSessionManager sessionManager;

  public static void create(MolgenisSessionManager sm) {
    sessionManager = sm;
    // framework
    get("/api/beacon", BeaconApi::getInfo);
    get("/api/beacon/", BeaconApi::getInfo);
    get("/api/beacon/info", BeaconApi::getInfo);
    get("/api/beacon/service-info", BeaconApi::getInfo);
    get("/api/beacon/configuration", BeaconApi::getConfiguration);
    get("/api/beacon/map", BeaconApi::getMap);
    get("/api/beacon/entry_types", BeaconApi::getEntryTypes);
    get("/api/beacon/datasets", BeaconApi::getDatasets);
    get("/api/beacon/g_variants", BeaconApi::getGenomicVariants);
    get("/api/beacon/analyses", BeaconApi::getAnalyses);

    /*
    both GET and POST are used to retrieve data, implement both?
    https://docs.genomebeacons.org/variant-queries/#beacon-sequence-queries
     */

    //    get("/:schema/api/beacon/filtering_terms", BeaconApi::getFilteringTerms);
    //
    //    // datasets model
    //    get("/:schema/api/beacon/datasets", BeaconApi::getDatasets);
    //    get("/:schema/api/beacon/datasets/:table", BeaconApi::getDatasetsForTable);
    //    // these are the interesting queries
    //    post("/:schema/api/beacon/datasets", BeaconApi::postDatasets);
    //    post("/:schema/api/beacon/datasets/:table", BeaconApi::postDatasetsForTable);
  }

  private static String getInfo(Request req, Response res) throws JsonProcessingException {

    return getWriter().writeValueAsString(new Info());
  }

  private static String getServiceInfo(Request request, Response response)
      throws JsonProcessingException {
    return getWriter().writeValueAsString(new ServiceInfo());
  }

  private static Object getConfiguration(Request request, Response response)
      throws JsonProcessingException {
    return getWriter().writeValueAsString(new Configuration());
  }

  private static Object getMap(Request request, Response response) throws JsonProcessingException {
    return getWriter().writeValueAsString(new Map(request));
  }

  private static Object getEntryTypes(Request request, Response response)
      throws JsonProcessingException {
    return getWriter().writeValueAsString(new EntryTypes());
  }

  private static String getFilteringTerms(Request request, Response response)
      throws JsonProcessingException {
    String skip = request.queryParams("skip");
    String limit = request.queryParams("limit");
    // TODO handle skip and limit
    return getWriter().writeValueAsString(new BeaconFilteringTermsResponse());
  }

  private static String getDatasets(Request request, Response response)
      throws JsonProcessingException {
    String skip = request.queryParams("skip");
    String limit = request.queryParams("limit");

    // TODO pass request to response to set limits, offsets etc
    // result should be BeaconBooleanResponse, BeaconCountResponse or BeaconCollectionResponse
    return getWriter().writeValueAsString(new Datasets(request, getSchemaNames(request)));
  }

  private static String getAnalyses(Request request, Response response) throws Exception {
    List<Table> tables = getTableFromAllSchemas("Analyses", request);
    return getWriter().writeValueAsString(new Analyses(request, tables));
  }

  private static String getGenomicVariants(Request request, Response response) throws Exception {
    List<Table> tables = getTableFromAllSchemas("GenomicVariations", request);
    return getWriter().writeValueAsString(new GenomicVariants(request, tables));
  }

  private static List<Table> getTableFromAllSchemas(String tableName, Request request) {
    List<Table> tables = new ArrayList<>();
    Collection<String> schemaNames = MolgenisWebservice.getSchemaNames(request);
    for (String sn : schemaNames) {
      Schema schema = sessionManager.getSession(request).getDatabase().getSchema(sn);
      Table t = schema.getTable(tableName);
      if (t != null) {
        tables.add(t);
      }
    }
    return tables;
  }

  private static String postDatasets(Request request, Response response)
      throws JsonProcessingException {
    // should parse body into
    BeaconRequestBody requestBody = null; // todo

    // result should be BeaconBooleanResponse, BeaconCountResponse or BeaconCollectionResponse
    return getWriter().writeValueAsString(null);
  }

  private static Object getDatasetsForTable(Request request, Response response)
      throws JsonProcessingException {

    // result should be BeaconBooleanResponse, BeaconCountResponse or beaconResultsetsResponse
    return getWriter().writeValueAsString(null);
  }

  private static Object postDatasetsForTable(Request request, Response response)
      throws JsonProcessingException {

    // should parse body into
    BeaconRequestBody requestBody = null; // todo

    // result should be BeaconBooleanResponse, BeaconCountResponse or beaconResultsetsResponse
    return getWriter().writeValueAsString(null);
  }
}
