package com.example.service.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WidgetRepositoryIntegrationTest {
  @Container
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Autowired private WidgetRepository repository;

  @Test
  void migrationRunsAndRepositoryRoundTrips() {
    Widget widget =
        new Widget(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "integration-example",
            Instant.parse("2026-01-01T00:00:00Z"));

    repository.save(widget);

    assertThat(repository.findById(widget.id())).contains(widget);
    assertThat(repository.findAll(10)).contains(widget);
  }
}
