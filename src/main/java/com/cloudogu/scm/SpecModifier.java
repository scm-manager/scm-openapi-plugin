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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import sonia.scm.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

class SpecModifier {

  private SpecModifier() {

  }

  static void configureServer(HttpServletRequest request, JsonNode spec) {
    String serverUrl = HttpUtil.getCompleteUrl(request, "api");

    ObjectNode serverNode = new ObjectNode(JsonNodeFactory.instance);
    serverNode.set("url", new TextNode(serverUrl));

    ((ObjectNode) spec).set("servers", new ArrayNode(JsonNodeFactory.instance, Collections.singletonList(serverNode)));
  }

}
