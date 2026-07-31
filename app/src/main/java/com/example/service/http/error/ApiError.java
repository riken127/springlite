package com.example.service.http.error;

/** Stable error envelope shared by all HTTP failures. */
public record ApiError(String code, String message, String correlationId) {}
