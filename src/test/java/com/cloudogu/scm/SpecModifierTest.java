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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecModifierTest {

  @Mock
  private HttpServletRequest request;

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void shouldConfigureServer() throws IOException {
    mockRequest("https://scm.hitchhiker.com", "/scm", "/api");
    JsonNode node = readJsonNode("scm-openapi.json");

    SpecModifier.configureServer(request, node);

    Iterator<JsonNode> servers = node.get("servers").iterator();
    JsonNode server = servers.next();

    assertThat(server.get("url").asText()).isEqualTo("https://scm.hitchhiker.com/scm/api");
    assertThat(server.has("description")).isFalse();
    assertThat(servers.hasNext()).isFalse();
  }

  @Test
  void shouldConfigureOnlyOneServer() throws IOException {
    mockRequest("https://hitchhiker.com", "/scm", "/api");
    JsonNode node = readJsonNode("multi-server.json");

    SpecModifier.configureServer(request, node);

    Iterator<JsonNode> servers = node.get("servers").iterator();
    servers.next();
    assertThat(servers.hasNext()).isFalse();
  }

  @Test
  void shouldConfigureServerEvenWithoutExistingServer() throws IOException {
    mockRequest("https://hitchhiker.com", "/scmv2", "/api");
    JsonNode node = readJsonNode("hg-openapi.json");

    SpecModifier.configureServer(request, node);

    Iterator<JsonNode> servers = node.get("servers").iterator();
    servers.next();
    assertThat(servers.hasNext()).isFalse();
  }

  private void mockRequest(String baseUrl, String contextPath, String path) {
    String requestURL = baseUrl + contextPath + path;

    when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL));
    when(request.getRequestURI()).thenReturn(contextPath + path);
    when(request.getContextPath()).thenReturn(contextPath);
  }

  private JsonNode readJsonNode(String name) throws IOException {
    URL resource = Resources.getResource("com/cloudogu/scm/" + name);
    return mapper.readTree(resource);
  }

}
