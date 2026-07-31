package com.example.service.application.query;

import java.util.UUID;

/** Raised when the requested widget does not exist. */
public final class WidgetNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final UUID widgetId;

  /** Creates the error for a missing widget identifier. */
  public WidgetNotFoundException(UUID widgetId) {
    super("Widget was not found");
    this.widgetId = widgetId;
  }

  /** Returns the requested identifier for internal diagnostics. */
  public UUID widgetId() {
    return widgetId;
  }
}
