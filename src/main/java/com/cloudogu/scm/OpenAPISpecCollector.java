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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sonia.scm.NotFoundException;
import sonia.scm.plugin.PluginLoader;

import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class OpenAPISpecCollector {

  private final ClassLoader uberClassLoader;
  private final ObjectMapper mapper = new ObjectMapper();

  @Inject
  public OpenAPISpecCollector(PluginLoader pluginLoader) {
    this.uberClassLoader = pluginLoader.getUberClassLoader();
  }

  public JsonNode getMergedOpenAPISpecs() throws IOException {
    Enumeration<URL> resources = uberClassLoader.getResources("META-INF/scm/openapi.json");
    if (!resources.hasMoreElements()) {
      throw new NotFoundException("OpenAPI-Spec", "all");
    }

    JsonNode mainNode = null;

    List<JsonNode> updateNodes = new ArrayList<>();
    while (resources.hasMoreElements()) {
      JsonNode jsonNode = mapper.readTree(resources.nextElement());
      if (isMainNode(jsonNode)) {
        mainNode = jsonNode;
      } else {
        updateNodes.add(jsonNode);
      }
    }
    if (mainNode == null) {
      throw new NotFoundException("OpenAPI-Spec", "mainNode");
    }

    for (JsonNode updateNode : updateNodes) {
      merge(mainNode, updateNode, "paths");
      merge(mainNode, updateNode, "tags");
      if (updateNode.has("components")) {
        merge(mainNode.get("components"), updateNode.get("components"), "schemas");
      }
    }

    return mainNode;
  }

  private boolean isMainNode(JsonNode jsonNode) {
    if (jsonNode.has("info") && jsonNode.get("info").has("contact") && jsonNode.get("info").get("contact").has("url")) {
      return jsonNode.get("info").get("contact").get("url").asText().equals("https://scm-manager.org");
    }
    return false;
  }


  private void merge(JsonNode main, JsonNode update, String fieldName) {
    if (update.has(fieldName)) {
      merge(main.get(fieldName), update.get(fieldName));
    }
  }

  private void merge(JsonNode mainNode, JsonNode updateNode) {
    if (mainNode.isArray()) {
      mergeArray((ArrayNode) mainNode, updateNode);
    } else {
      mergeObject(mainNode, updateNode);
    }
  }

  private void mergeObject(JsonNode mainNode, JsonNode updateNode) {
    if (updateNode == null) {
      return;
    }

    Iterator<String> fieldNames = updateNode.fieldNames();

    while (fieldNames.hasNext()) {

      String fieldName = fieldNames.next();
      JsonNode jsonNode = mainNode.get(fieldName);

      if (jsonNode != null) {
        mergeNode(updateNode, fieldName, jsonNode);
      } else {
        mergeField(mainNode, updateNode, fieldName);
      }
    }
  }

  private void mergeArray(ArrayNode mainNode, JsonNode updateNode) {
    for (JsonNode child : updateNode) {
      mainNode.add(child);
    }
  }

  private void mergeField(JsonNode mainNode, JsonNode updateNode, String fieldName) {
    if (mainNode instanceof ObjectNode) {
      JsonNode value = updateNode.get(fieldName);
      if (value.isNull()) {
        return;
      }
      if (value.isIntegralNumber() && value.toString().equals("0")) {
        return;
      }
      if (value.isFloatingPointNumber() && value.toString().equals("0.0")) {
        return;
      }
      ((ObjectNode) mainNode).set(fieldName, value);
    }
  }

  private void mergeNode(JsonNode updateNode, String fieldName, JsonNode jsonNode) {
    if (jsonNode.isObject()) {
      merge(jsonNode, updateNode.get(fieldName));
    } else if (jsonNode.isArray()) {
      for (int i = 0; i < jsonNode.size(); i++) {
        merge(jsonNode.get(i), updateNode.get(fieldName).get(i));
      }
    }
  }
}
