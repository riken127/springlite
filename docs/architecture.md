# Architecture

Springlite is a starting point, not a reusable framework. It brings javelite's small classes,
visible wiring, few dependencies, and honest omissions to Spring Boot while keeping the official
Boot AOT model intact.

## Shape and dependency direction

There is one deployable Maven module. Package boundaries delay module ceremony until independent
release cadence, ownership, or build isolation makes another module worthwhile.

```text
http ───────┐
persistence ├──> application ──> domain
bootstrap ──┘
```

- `bootstrap`: the explicit `@Bean` graph and integration between adapters and use cases.
- `config`: validated `@ConfigurationProperties`; no scattered `@Value` fields.
- `http`: Spring MVC request/response types, validation, mapping, and centralized errors.
- `application`: commands, queries, orchestration, and persistence ports.
- `domain`: plain Java rules with no Spring, HTTP, or jOOQ types.
- `persistence`: jOOQ statements and record-to-domain mapping.
- `nativeimage`: reserved for proven custom runtime hints. It is empty until a dependency needs one.
- `observability`: correlation, readiness integration, and application metrics.

ArchUnit protects only the most valuable directions. It is a guardrail, not a complete architecture
language.

## Deliberate choices

Spring MVC matches the imperative JDBC workload and virtual-thread execution model. Annotated
controllers are used consistently because Bean Validation and centralized errors are immediately
recognizable; controllers are still created explicitly in `ApplicationConfiguration`, so the graph
does not depend on application component scanning.

jOOQ retains explicit SQL and mapping without Hibernate's persistence model. The base uses manual
typed fields so first use never requires a running database for code generation. Add reproducible
jOOQ code generation from migrations when the schema size makes generated types valuable.

Constructor injection is the only injection style. Spring owns lifecycle, configuration,
auto-configuration, HTTP, metrics, and AOT analysis; application/domain behavior remains plain Java.

Virtual threads suit blocking MVC/JDBC code, but they do not enlarge the database. The Hikari pool
defaults to five connections. Avoid long `synchronized`/native sections that pin carriers, observe
pinning before tuning, and benchmark the actual workload.

## Deliberate omissions

No JPA/Hibernate, Spring Data, Lombok, MapStruct, WebFlux, R2DBC, Spring Cloud, OpenFeign, Kafka,
Redis, generic authentication, vendor telemetry, Kubernetes, base controllers/services/repositories,
or interface-per-class policy. Introduce any of these only with a measured requirement and native
compatibility evidence.

This template is a poor fit for reactive end-to-end systems, UI monoliths, services whose primary
model requires JPA, or platforms that already mandate a different application chassis.
