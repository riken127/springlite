# Benchmarks

Run `make compare-runtimes` with Docker, Java 25, GraalVM Native Image, `curl`, and `oha`
installed. The harness uses two warmed workloads: dynamic JSON without I/O and a real PostgreSQL
read. It records readiness time, idle/loaded RSS, and complete oha JSON (p50/p95/p99, throughput,
errors, and timing distributions).

Local results are ignored except for the documented example. Configure `BENCHMARK_DURATION`,
`BENCHMARK_CONNECTIONS`, `MAX_STARTUP_MS`, `MAX_IDLE_RSS_MB`, `MAX_P99_MS`, and
`MIN_REQUESTS_PER_SECOND` in a service-specific CI wrapper. Thresholds are intentionally optional;
shared CI hardware is not a stable universal performance oracle.
