package org.molgenis.emx2.beaconv2.responses.datasets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Collection {

  String id;
  String name;
  String createDataTime;
  String updateDataTime;

  public Collection(String id, String name, String createDataTime, String updateDataTime) {
    this.id = id;
    this.name = name;
    this.createDataTime = createDataTime;
    this.updateDataTime = updateDataTime;
  }
}
