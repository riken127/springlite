# Springlite

A native-first Spring Boot template for small, production-oriented Java microservices.

Springlite applies to Spring Boot the same principles of simplicity, clarity, and architectural
discipline found in [javelite](https://github.com/riken127/javelite). Javelite keeps wiring explicit
without Spring; Springlite uses Spring Boot deliberately, with an explicit object graph and
continuous AOT/Native Image support. This repository is a GitHub template, not a Spring Framework
fork, library, or distribution.

Suggested GitHub description:

> Native-first Spring Boot microservice template with Java 25, GraalVM, jOOQ, PostgreSQL, Testcontainers, Google Java Style, and reproducible JVM/native benchmarks.

Suggested topics: `java`, `spring-boot`, `spring-template`, `microservice-template`, `graalvm`,
`native-image`, `aot`, `virtual-threads`, `jooq`, `postgresql`, `flyway`, `testcontainers`,
`google-java-format`, `checkstyle`, `error-prone`.

## Quick start

After clicking **Use this template**:

```bash
scripts/init-template.sh \
  --service-name orders-service \
  --package com.acme.orders \
  --group-id com.acme

make format
make verify
make dev
```

The initializer also accepts custom root/app artifact IDs and database name. Use `--dry-run` to
review its complete plan.

## What is included

- Java 25, Spring Boot 4.1, Spring MVC, virtual threads, Actuator, and Micrometer
- explicit constructor wiring; framework-free domain; ArchUnit dependency rules
- jOOQ with explicit SQL mapping, PostgreSQL, HikariCP, and Flyway (no JPA/Hibernate)
- JVM, Java 25 AOT Cache, and GraalVM Native Image runtime modes
- JUnit 5, AssertJ, Testcontainers, Error Prone, Spotless, Google Java Format and Checkstyle
- non-root JVM/native images, local Compose, CI, native smoke testing, and benchmarks

The removable `Widget` example exposes:

```text
POST /v1/widgets
GET  /v1/widgets/{id}
GET  /v1/widgets
GET  /v1/system/ping
GET  /actuator/health/liveness
GET  /actuator/health/readiness
GET  /actuator/prometheus
```

## Commands and runtime modes

```bash
make help
make run                 # normal HotSpot JVM
make package-aot-cache   # Java 25 AOT Cache training
make run-aot-cache
make native-build
make native-smoke
make run-native
make image-jvm           # springlite:jvm
make image-native        # springlite:native
make compare-runtimes
```

The JVM remains a first-class choice for long-running and CPU-heavy services. Native Image targets
startup, readiness, memory footprint, and no-warm-up predictability; it does not promise better
peak throughput than HotSpot. See [runtime modes](docs/runtime-modes.md) and
[performance](docs/performance.md).

## Repository map

```text
app/            single deployable Maven module
benchmarks/     transparent CPU and PostgreSQL workloads
docs/           architecture, native, runtime, performance, and usage guides
scripts/        initialization, smoke tests, AOT Cache, and measurement
.github/        CI, native validation, benchmarks, and Dependabot
```

Start with [template usage](docs/template-usage.md), [architecture](docs/architecture.md),
[Native Image](docs/native-image.md), and [agent guidance](AGENTS.md). The base intentionally omits
authentication, messaging, caches, Spring Cloud, WebFlux, Kubernetes manifests and vendor tracing;
add only what a concrete service requires.
