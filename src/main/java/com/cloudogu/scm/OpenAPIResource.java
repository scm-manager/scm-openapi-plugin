/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm;

import com.fasterxml.jackson.databind.JsonNode;
import sonia.scm.security.AllowAnonymousAccess;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
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
  @Path("")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonNode getMergedOpenApiSpec(@Context HttpServletRequest request) throws IOException {
    JsonNode mergedSpec = collector.getMergedOpenAPISpecs();
    SpecModifier.configureServer(request, mergedSpec);
    return mergedSpec;
  }

}
