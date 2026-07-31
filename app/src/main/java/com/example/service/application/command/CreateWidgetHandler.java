package com.example.service.application.command;

import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Creates one widget and delegates persistence through the application port. */
public final class CreateWidgetHandler {
  private final WidgetRepository repository;
  private final Clock clock;

  /** Creates the use case with persistence and time dependencies. */
  public CreateWidgetHandler(WidgetRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  /** Validates, creates, and stores a widget. */
  public Widget handle(CreateWidgetCommand command) {
    String name = command.name() == null ? null : command.name().trim();
    return repository.save(new Widget(UUID.randomUUID(), name, Instant.now(clock)));
  }
}
