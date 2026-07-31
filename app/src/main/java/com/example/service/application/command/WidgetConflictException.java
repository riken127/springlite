package com.example.service.application.command;

/** Raised when a widget violates a stable uniqueness rule. */
public final class WidgetConflictException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Creates a conflict while retaining its persistence cause for logging. */
  public WidgetConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
