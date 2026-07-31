package com.example.service.observability;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/** Records time-to-readiness once without introducing a vendor exporter. */
public final class ApplicationStartupMetrics implements ApplicationListener<ApplicationReadyEvent> {
  private final long startedAt = System.nanoTime();
  private final MeterRegistry registry;

  /** Creates the listener backed by the application meter registry. */
  public ApplicationStartupMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    registry
        .timer("service.ready.latency")
        .record(System.nanoTime() - startedAt, TimeUnit.NANOSECONDS);
  }
}
