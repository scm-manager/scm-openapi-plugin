package com.cloudogu.scm;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
import sonia.scm.plugin.PluginLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAPISpecCollectorTest {

  @Mock
  private ClassLoader uberClassLoader;

  @Mock
  private PluginLoader pluginLoader;

  private OpenAPISpecCollector collector;

  @BeforeEach
  void setUpMocks() {
    when(pluginLoader.getUberClassLoader()).thenReturn(uberClassLoader);
    collector = new OpenAPISpecCollector(pluginLoader);
  }

  @Test
  void shouldReturnSingleOpenAPISpec() throws IOException {
    bindResourceEnum("scm-openapi.json");

    JsonNode node = collector.getMergedOpenAPISpecs();

    assertThat(node.get("info").get("title").asText()).isEqualTo("SCM-Manager REST-API");
  }

  @Test
  void shouldFailIfNoSpecCouldBeFound() throws IOException {
    when(uberClassLoader.getResources("META-INF/scm/openapi.json")).thenReturn(Collections.emptyEnumeration());

    assertThrows(NotFoundException.class, () -> collector.getMergedOpenAPISpecs());
  }

  @Test
  void shouldMergePaths() throws IOException {
    bindResourceEnum("scm-openapi.json", "git-openapi.json", "hg-openapi.json");

    JsonNode root = collector.getMergedOpenAPISpecs();
    JsonNode paths = root.get("paths");
    assertThat(paths.has("/v2/auth/access_token")).isTrue();
    assertThat(paths.has("/v2/config/git")).isTrue();
    assertThat(paths.has("/v2/config/hg")).isTrue();
  }

  @Test
  void shouldMergeTags() throws IOException {
    bindResourceEnum("scm-openapi.json", "git-openapi.json", "hg-openapi.json");

    JsonNode root = collector.getMergedOpenAPISpecs();

    JsonNode tags = root.get("tags");
    assertThat(tags.isArray()).isTrue();

    List<String> tagNames = StreamSupport.stream(tags.spliterator(), false)
      .map(node -> node.get("name").asText())
      .collect(Collectors.toList());
    assertThat(tagNames).containsExactlyInAnyOrder("Repository", "Git", "Hg");
  }

  @Test
  void shouldMergeSchemas() throws IOException {
    bindResourceEnum("scm-openapi.json", "git-openapi.json", "hg-openapi.json");

    JsonNode root = collector.getMergedOpenAPISpecs();

    JsonNode schemas = root.get("components").get("schemas");
    assertThat(schemas.has("FileDto")).isTrue();
    assertThat(schemas.has("GitConfigDto")).isTrue();
    assertThat(schemas.has("HgConfigDto")).isTrue();
  }

  @Test
  void shouldFailIfNoMainNodeCouldBeFound() throws IOException {
    bindResourceEnum("git-openapi.json", "hg-openapi.json");

    assertThrows(NotFoundException.class, () -> collector.getMergedOpenAPISpecs());
  }

  @Test
  void shouldKeepInfoFromCore() throws IOException {
    bindResourceEnum("git-openapi.json", "hg-openapi.json", "scm-openapi.json");

    JsonNode root = collector.getMergedOpenAPISpecs();

    assertThat(root.get("info").get("title").asText()).isEqualTo("SCM-Manager REST-API");
  }

  @Test
  void shouldNotFailForEmptySpecs() throws IOException {
    bindResourceEnum("empty-openapi.json", "scm-openapi.json");

    JsonNode root = collector.getMergedOpenAPISpecs();

    assertThat(root.get("info").get("title").asText()).isEqualTo("SCM-Manager REST-API");
  }

  private void bindResourceEnum(String... resources) throws IOException {
    Enumeration<URL> resourceEnum = createResourceEnum(resources);
    when(uberClassLoader.getResources("META-INF/scm/openapi.json")).thenReturn(resourceEnum);
  }

  private Enumeration<URL> createResourceEnum(String... resources) throws IOException {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    List<Enumeration<URL>> urlResources = new ArrayList<>();
    for (String resource : resources) {
      urlResources.add(contextClassLoader.getResources("com/cloudogu/scm/" + resource));
    }
    return concat(urlResources.toArray(new Enumeration[0]));
  }

  @SuppressWarnings("unchecked")
  private <T> Enumeration<T> concat(Enumeration<T>... enumerations) {
    List<Iterable<T>> iterables = Arrays.stream(enumerations)
      .map(Iterators::forEnumeration)
      .map(it -> (Iterable<T>) () -> it)
      .collect(Collectors.toList());
    return Iterators.asEnumeration(Iterables.concat(iterables).iterator());
  }

}
