package com.example.service.http;

import com.example.service.application.command.CreateWidgetCommand;
import com.example.service.application.command.CreateWidgetHandler;
import com.example.service.application.query.GetWidgetHandler;
import com.example.service.application.query.ListWidgetsHandler;
import com.example.service.http.request.CreateWidgetRequest;
import com.example.service.http.response.WidgetResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Thin HTTP adapter for the removable Widget example. */
@RestController
@RequestMapping("/v1/widgets")
public final class WidgetController {
  private final CreateWidgetHandler createWidget;
  private final GetWidgetHandler getWidget;
  private final ListWidgetsHandler listWidgets;

  /** Creates the adapter with its three explicit use cases. */
  public WidgetController(
      CreateWidgetHandler createWidget,
      GetWidgetHandler getWidget,
      ListWidgetsHandler listWidgets) {
    this.createWidget = createWidget;
    this.getWidget = getWidget;
    this.listWidgets = listWidgets;
  }

  /** Validates and creates a widget. */
  @PostMapping
  public ResponseEntity<WidgetResponse> create(@Valid @RequestBody CreateWidgetRequest request) {
    WidgetResponse response =
        WidgetResponse.from(createWidget.handle(new CreateWidgetCommand(request.name())));
    return ResponseEntity.created(URI.create("/v1/widgets/" + response.id())).body(response);
  }

  /** Finds one widget by public identifier. */
  @GetMapping("/{id}")
  public WidgetResponse findById(@PathVariable UUID id) {
    return WidgetResponse.from(getWidget.handle(id));
  }

  /** Lists a bounded number of widgets. */
  @GetMapping
  public List<WidgetResponse> findAll(@RequestParam(required = false) Integer limit) {
    return listWidgets.handle(limit).stream().map(WidgetResponse::from).toList();
  }
}
