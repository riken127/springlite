package com.example.service.application.query;

import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.util.UUID;

/** Loads one widget or reports the stable not-found application error. */
public final class GetWidgetHandler {
  private final WidgetRepository repository;

  /** Creates the query with its persistence port. */
  public GetWidgetHandler(WidgetRepository repository) {
    this.repository = repository;
  }

  /** Returns the widget or raises a not-found application error. */
  public Widget handle(UUID id) {
    return repository.findById(id).orElseThrow(() -> new WidgetNotFoundException(id));
  }
}
