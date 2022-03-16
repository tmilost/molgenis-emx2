package org.molgenis.emx2;

import static org.molgenis.emx2.ColumnType.INT;
import static org.molgenis.emx2.sql.SqlDatabase.ADMIN_USER;

import org.molgenis.emx2.examples.PetStoreExample;
import org.molgenis.emx2.sql.SqlDatabase;
import org.molgenis.emx2.utils.EnvironmentProperty;
import org.molgenis.emx2.web.MolgenisWebservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunMolgenisEmx2 {

  private static Logger logger = LoggerFactory.getLogger(RunMolgenisEmx2.class);

  public static void main(String[] args) {
    logger.info("Starting MOLGENIS EMX2 Software Version=" + Version.getVersion());

    Integer port =
        (Integer) EnvironmentProperty.getParameter(Constants.MOLGENIS_HTTP_PORT, "8080", INT);
    logger.info(
        "with "
            + org.molgenis.emx2.Constants.MOLGENIS_HTTP_PORT
            + "="
            + port
            + " (change either via java properties or via ENV variables)");

    // setup database
    Database db = new SqlDatabase(true);

    // elevate privileges for init
    db.becomeAdmin();
    if (db.getSchema("pet store") == null) {
      Schema schema = db.createSchema("pet store");
      PetStoreExample.create(schema.getMetadata());
      PetStoreExample.populate(schema);
    }
    // remove admin
    db.clearActiveUser();

    // start
    MolgenisWebservice.start(port);
  }
}
