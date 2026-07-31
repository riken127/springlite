# Performance

The benchmark harness compares JVM, Java 25 AOT Cache, and Native Image against the same PostgreSQL
container. It warms and measures two paths: `/v1/system/ping` (minimal CPU/JSON) and
`/v1/widgets?limit=20` (real JDBC/jOOQ read and mapping).

`oha` records p50/p95/p99, throughput, errors, and timing distributions. The shell harness records
time-to-readiness and process RSS before and after load. Container image and executable sizes can
be added from `docker image inspect` and `du`; build wall time is intentionally kept visible in CI
logs. Run long enough to expose JIT warm-up and report warm-up separately from steady state.

```bash
make compare-runtimes
BENCHMARK_DURATION=60s BENCHMARK_CONNECTIONS=50 make compare-runtimes
```

RSS includes shared/native allocations and is not Java heap. Percentiles describe a distribution;
an average cannot replace p99. Throughput is meaningful only with error rate and CPU saturation.
Results change with hardware, OS, database, pool/heap settings, CPU temperature, frequency scaling,
background load, and container runtime. Shared CI is useful for regressions, not universal claims.

Service owners may enforce optional `MAX_STARTUP_MS`, `MAX_IDLE_RSS_MB`, `MAX_P99_MS`, and
`MIN_REQUESTS_PER_SECOND` after establishing a target-hardware baseline. The template ships no
fabricated budget.
