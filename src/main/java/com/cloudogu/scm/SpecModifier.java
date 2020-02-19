package com.cloudogu.scm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import sonia.scm.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;
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
