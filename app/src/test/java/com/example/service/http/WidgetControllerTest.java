package com.example.service.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.service.application.command.CreateWidgetHandler;
import com.example.service.application.port.WidgetRepository;
import com.example.service.application.query.GetWidgetHandler;
import com.example.service.application.query.ListWidgetsHandler;
import com.example.service.domain.Widget;
import com.example.service.http.error.GlobalExceptionHandler;
import com.example.service.observability.CorrelationIdFilter;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class WidgetControllerTest {
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    InMemoryWidgetRepository repository = new InMemoryWidgetRepository();
    WidgetController controller =
        new WidgetController(
            new CreateWidgetHandler(
                repository, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)),
            new GetWidgetHandler(repository),
            new ListWidgetsHandler(repository));
    mvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addFilters(new CorrelationIdFilter())
            .build();
  }

  @Test
  void createsAndReadsWidget() throws Exception {
    String body =
        mvc.perform(
                post("/v1/widgets")
                    .header("X-Correlation-Id", "test-request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"example\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("X-Correlation-Id", "test-request"))
            .andExpect(jsonPath("$.name").value("example"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    String id = body.substring(body.indexOf("\"id\":\"") + 6, body.indexOf("\",\"name\""));

    mvc.perform(get("/v1/widgets/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("example"));
  }

  @Test
  void mapsValidationAndNotFoundErrors() throws Exception {
    mvc.perform(
            post("/v1/widgets").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

    mvc.perform(get("/v1/widgets/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("WIDGET_NOT_FOUND"));
  }

  private static final class InMemoryWidgetRepository implements WidgetRepository {
    private final List<Widget> widgets = new ArrayList<>();

    @Override
    public Widget save(Widget widget) {
      widgets.add(widget);
      return widget;
    }

    @Override
    public Optional<Widget> findById(UUID id) {
      return widgets.stream().filter(widget -> widget.id().equals(id)).findFirst();
    }

    @Override
    public List<Widget> findAll(int limit) {
      return widgets.stream().limit(limit).toList();
    }
  }
}
