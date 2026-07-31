package com.example.service.application.query;

import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.util.List;

/** Lists recent widgets with a deliberately bounded result size. */
public final class ListWidgetsHandler {
  private static final int DEFAULT_LIMIT = 20;
  private static final int MAX_LIMIT = 100;

  private final WidgetRepository repository;

  /** Creates the query with its persistence port. */
  public ListWidgetsHandler(WidgetRepository repository) {
    this.repository = repository;
  }

  /** Applies list bounds and returns domain widgets. */
  public List<Widget> handle(Integer requestedLimit) {
    int limit = requestedLimit == null ? DEFAULT_LIMIT : requestedLimit;
    if (limit < 1) {
      throw new IllegalArgumentException("limit must be greater than zero");
    }
    return repository.findAll(Math.min(limit, MAX_LIMIT));
  }
}
