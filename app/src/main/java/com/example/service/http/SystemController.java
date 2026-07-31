package com.example.service.http;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Database-free endpoint used by the minimal-CPU benchmark workload. */
@RestController
@RequestMapping("/v1/system")
public final class SystemController {
  /** Returns a small dynamic response without performing I/O. */
  @GetMapping("/ping")
  public Map<String, Object> ping() {
    return Map.of("status", "ok", "timestamp", Instant.now().toString());
  }
}
