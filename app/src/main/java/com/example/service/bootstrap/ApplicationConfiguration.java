package com.example.service.bootstrap;

import com.example.service.application.command.CreateWidgetHandler;
import com.example.service.application.port.WidgetRepository;
import com.example.service.application.query.GetWidgetHandler;
import com.example.service.application.query.ListWidgetsHandler;
import com.example.service.http.SystemController;
import com.example.service.http.WidgetController;
import com.example.service.http.error.GlobalExceptionHandler;
import com.example.service.observability.ApplicationStartupMetrics;
import com.example.service.observability.CorrelationIdFilter;
import com.example.service.persistence.jooq.JooqWidgetRepository;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Single, explicit location for the application dependency graph. */
@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {
  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  WidgetRepository widgetRepository(DSLContext dsl) {
    return new JooqWidgetRepository(dsl);
  }

  @Bean
  CreateWidgetHandler createWidgetHandler(WidgetRepository repository, Clock clock) {
    return new CreateWidgetHandler(repository, clock);
  }

  @Bean
  GetWidgetHandler getWidgetHandler(WidgetRepository repository) {
    return new GetWidgetHandler(repository);
  }

  @Bean
  ListWidgetsHandler listWidgetsHandler(WidgetRepository repository) {
    return new ListWidgetsHandler(repository);
  }

  @Bean
  WidgetController widgetController(
      CreateWidgetHandler createWidget,
      GetWidgetHandler getWidget,
      ListWidgetsHandler listWidgets) {
    return new WidgetController(createWidget, getWidget, listWidgets);
  }

  @Bean
  SystemController systemController() {
    return new SystemController();
  }

  @Bean
  GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Bean
  CorrelationIdFilter correlationIdFilter() {
    return new CorrelationIdFilter();
  }

  @Bean
  ApplicationStartupMetrics applicationStartupMetrics(MeterRegistry registry) {
    return new ApplicationStartupMetrics(registry);
  }
}
