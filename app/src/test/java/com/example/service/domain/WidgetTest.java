package com.example.service.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WidgetTest {
  @Test
  void rejectsBlankNames() {
    assertThatThrownBy(() -> new Widget(UUID.randomUUID(), " ", Instant.EPOCH))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Widget name must not be blank");
  }
}
