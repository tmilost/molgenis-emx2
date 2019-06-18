package org.molgenis.emx2;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class MolgenisMain {
  private static Logger logger = Logger.getLogger("hello");

  public static void main(String[] args) {
    port(8080);
    get("/api", (req, res) -> listGroups());
    get("/api/:group", (req, res) -> listTablesForGroup(req.params("group")));
    get("/api/:group/:table", (req, res) -> "first " + req.params(("table")));
    patch("/api/:group", (req, res) -> parse(req.body()));

    // handling of exceptions
    exception(
        JsonException.class,
        (e, req, res) -> {
          res.status(400);
          res.body(
              String.format("{\"message\":\"%s\n%s\"\n}", "Failed to parse JSON:", req.body()));
        });
  }

  private static String listGroups() {
    return "todo: list groups";
  }

  private static String listTablesForGroup(String group) {
    return "todo: list tables of group '" + group + "'";
  }

  private static String parse(String input) {
    Any any = JsonIterator.deserialize(input);
    logger.info("blaat=" + any.get("blaat").toLong());
    logger.info("blaat2=" + any.get("blaat2").toLong());
    return "success";
  }
}
