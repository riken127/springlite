# Template usage

Initialize exactly once after **Use this template**:

```bash
scripts/init-template.sh --service-name orders-service --package com.acme.orders --group-id com.acme
make format
make verify
make dev
```

Use `--database-name`, `--root-artifact-id`, `--app-artifact-id`, and `--dry-run` when defaults do
not match. The script validates before editing, moves main/test package trees, updates Maven,
containers, images, configuration, scripts and documentation, while preserving itself for audit.

To add behavior, put domain rules in `domain`, a command/query and port in `application`, implement
SQL/mapping in `persistence`, wire it in `bootstrap`, and expose a thin request/response adapter in
`http`. Add Flyway migrations rather than mutating existing applied migrations. Keep jOOQ types from
crossing the persistence boundary. Introduce jOOQ code generation only with a CI-reproducible schema
source.

Configuration belongs in validated `@ConfigurationProperties`; environment variables are listed in
`.env.example`. Never commit `.env` or secrets. Production structured logging is enabled with the
`prod` profile and must not include credentials, tokens, connection strings, unrestricted bodies,
or personal data.

Tests named `*Test` run with Surefire; `*IntegrationTest` run with Failsafe and Testcontainers.
Every change needs the smallest relevant test and `make format && make verify`. Native-sensitive
changes also need `make native-build && make native-smoke`.

The Widget example is intentionally removable: delete its migration, domain object, application
handlers/port, persistence adapter, HTTP DTO/controller, bean declarations, and tests together.
Keep the system endpoint and health infrastructure if benchmarks/operations still use them.

Graceful shutdown accepts SIGTERM in JVM and native modes, stops accepting readiness traffic, and
uses a 20-second Spring lifecycle phase timeout. Platforms should use readiness for traffic and
liveness only for a stuck process; liveness deliberately does not depend on PostgreSQL.
