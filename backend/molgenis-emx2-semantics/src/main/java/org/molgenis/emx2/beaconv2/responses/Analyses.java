package org.molgenis.emx2.beaconv2.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import org.molgenis.emx2.Table;
import org.molgenis.emx2.beaconv2.responses.analyses.AnalysesResponse;
import org.molgenis.emx2.beaconv2.responses.datasets.DatasetsMeta;
import org.molgenis.emx2.beaconv2.responses.datasets.ResponseSummary;
import spark.Request;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Analyses {

  DatasetsMeta meta;
  ResponseSummary responseSummary;
  AnalysesResponse response;

  public Analyses(Request request, List<Table> tables) throws Exception {
    this.meta = new DatasetsMeta("../beaconDatasetResponse.json", "datasets");
    this.response = new AnalysesResponse(request, tables);
    this.responseSummary = new ResponseSummary();
  }
}
