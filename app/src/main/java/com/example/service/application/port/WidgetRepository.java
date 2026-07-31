package com.example.service.application.port;

import com.example.service.domain.Widget;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Application-facing persistence contract; no jOOQ types cross this boundary. */
public interface WidgetRepository {
  /** Stores a new widget. */
  Widget save(Widget widget);

  /** Finds one widget by identifier. */
  Optional<Widget> findById(UUID id);

  /** Lists the most recent widgets up to a bounded limit. */
  List<Widget> findAll(int limit);
}
