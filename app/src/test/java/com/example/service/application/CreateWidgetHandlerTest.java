package com.example.service.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.service.application.command.CreateWidgetCommand;
import com.example.service.application.command.CreateWidgetHandler;
import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateWidgetHandlerTest {
  @Test
  void createsValidatedWidgetAtCurrentTime() {
    RecordingRepository repository = new RecordingRepository();
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    CreateWidgetHandler handler =
        new CreateWidgetHandler(repository, Clock.fixed(now, ZoneOffset.UTC));

    Widget created = handler.handle(new CreateWidgetCommand("  example  "));

    assertThat(created.name()).isEqualTo("example");
    assertThat(created.createdAt()).isEqualTo(now);
    assertThat(repository.saved).isEqualTo(created);
  }

  private static final class RecordingRepository implements WidgetRepository {
    private Widget saved;

    @Override
    public Widget save(Widget widget) {
      saved = widget;
      return widget;
    }

    @Override
    public Optional<Widget> findById(UUID id) {
      return Optional.empty();
    }

    @Override
    public List<Widget> findAll(int limit) {
      return List.of();
    }
  }
}
