package com.example.service.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/** Propagates a bounded caller correlation id through response headers and logging MDC. */
public final class CorrelationIdFilter extends OncePerRequestFilter {
  public static final String CORRELATION_ID_ATTRIBUTE =
      CorrelationIdFilter.class.getName() + ".correlationId";
  private static final String HEADER = "X-Correlation-Id";
  private static final int MAX_LENGTH = 128;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String correlationId = normalized(request.getHeader(HEADER));
    request.setAttribute(CORRELATION_ID_ATTRIBUTE, correlationId);
    response.setHeader(HEADER, correlationId);
    MDC.put("correlationId", correlationId);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove("correlationId");
    }
  }

  private String normalized(String candidate) {
    if (candidate == null || candidate.isBlank() || candidate.length() > MAX_LENGTH) {
      return UUID.randomUUID().toString();
    }
    return candidate.replaceAll("[^a-zA-Z0-9._:-]", "_");
  }
}
