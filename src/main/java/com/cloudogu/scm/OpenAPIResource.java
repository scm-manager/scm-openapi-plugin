package com.cloudogu.scm;

import com.fasterxml.jackson.databind.JsonNode;
import sonia.scm.security.AllowAnonymousAccess;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("v2/openapi")
@AllowAnonymousAccess
public class OpenAPIResource {

  private final OpenAPISpecCollector collector;

  @Inject
  public OpenAPIResource(OpenAPISpecCollector collector) {
    this.collector = collector;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public JsonNode getMergedOpenApiSpec() throws IOException {
    return collector.getMergedOpenAPISpecs();
  }

}
