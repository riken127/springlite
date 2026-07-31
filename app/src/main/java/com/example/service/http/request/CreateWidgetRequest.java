package com.example.service.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Validated HTTP representation for widget creation. */
public record CreateWidgetRequest(@NotBlank @Size(max = 100) String name) {}
