# Repository guidance

Springlite is a small, native-first Spring Boot template, not a framework. Preserve explicit wiring,
constructor injection, plain-Java domain code, and proportional complexity.

Before finishing a change run:

```bash
make format
make verify
```

For AOT/native-sensitive behavior also run `make native-build` and `make native-smoke`. Add focused
unit tests and realistic Testcontainers integration tests; never disable tests to pass CI.

Dependency direction is adapters (`http`, `persistence`) to `application` to `domain`. jOOQ types
stay in `persistence`. HTTP never queries the database directly. Put the dependency graph in
`bootstrap`, typed validated properties in `config`, and contextual operations concerns in
`observability`.

Do not add field injection, JPA/Hibernate, Spring Data repositories, Lombok, MapStruct, WebFlux,
R2DBC, Spring Cloud, generic base layers, interface-per-class abstractions, secrets, vendor-specific
telemetry, or infrastructure manifests without a concrete requirement and documented decision.

Avoid premature modules and universal abstractions. New dependencies need an actual use, a native
compatibility check, and documentation. New custom runtime hints belong in `nativeimage`, with the
reason, affected dependency/type/resource, and a corresponding test. Never commit unreviewed mass
reflection configuration.

Log through SLF4J without wrappers. Preserve correlation IDs and never log passwords, tokens,
connection strings, unrestricted bodies, or personal data. Evolve PostgreSQL through Flyway and map
records to domain objects before returning from persistence.
