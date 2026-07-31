package com.example.service.domain;

import java.time.Instant;
import java.util.UUID;

/** Framework-free domain object for the removable technical example. */
public record Widget(UUID id, String name, Instant createdAt) {
  /** Enforces the example's small domain invariants. */
  public Widget {
    if (id == null) {
      throw new IllegalArgumentException("Widget id must not be null");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Widget name must not be blank");
    }
    if (name.length() > 100) {
      throw new IllegalArgumentException("Widget name must not exceed 100 characters");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("Widget creation time must not be null");
    }
  }
}
