package com.example.service.http.response;

import com.example.service.domain.Widget;
import java.time.Instant;
import java.util.UUID;

/** Stable public representation that does not expose persistence types. */
public record WidgetResponse(UUID id, String name, Instant createdAt) {
  /** Maps a domain widget to its public representation. */
  public static WidgetResponse from(Widget widget) {
    return new WidgetResponse(widget.id(), widget.name(), widget.createdAt());
  }
}
