SHELL := /bin/sh

.DEFAULT_GOAL := help

.PHONY: help format format-check lint test integration-test architecture-test verify run dev smoke package-jvm package-aot-cache run-aot-cache native-build native-build-quick native-test native-smoke run-native image-jvm image-native benchmark-jvm benchmark-native compare-runtimes measure-startup measure-memory postgres-up postgres-down clean

help:
	@printf '%s\n' 'Springlite development targets:'
	@printf '  %-24s %s\n' 'make format' 'Apply Google Java Format'
	@printf '  %-24s %s\n' 'make verify' 'Run lint, unit, integration, and architecture tests'
	@printf '  %-24s %s\n' 'make dev' 'Start PostgreSQL and the JVM application'
	@printf '  %-24s %s\n' 'make package-aot-cache' 'Build a Java 25 AOT cache'
	@printf '  %-24s %s\n' 'make native-build' 'Build the GraalVM native executable'
	@printf '  %-24s %s\n' 'make compare-runtimes' 'Benchmark JVM, JVM+AOT, and Native'
	@printf '  %-24s %s\n' 'make image-jvm' 'Build springlite:jvm'
	@printf '  %-24s %s\n' 'make image-native' 'Build springlite:native'

format:
	./mvnw spotless:apply

format-check:
	./mvnw spotless:check

lint:
	./mvnw -DskipTests -DskipITs compile checkstyle:check

test:
	./mvnw test

integration-test:
	./mvnw -DskipTests failsafe:integration-test failsafe:verify

architecture-test:
	./mvnw -pl app -Dtest=ArchitectureTest test

verify:
	./mvnw clean verify

run:
	./mvnw -pl app spring-boot:run

dev: postgres-up run

smoke:
	scripts/smoke-test.sh http://localhost:$${HTTP_PORT:-8080}

package-jvm:
	./mvnw -DskipTests package

package-aot-cache:
	scripts/package-aot-cache.sh

run-aot-cache:
	cd app/application && exec java -XX:AOTCache=app.aot -jar springlite-app-0.1.0-SNAPSHOT.jar

native-build:
	./mvnw -pl app -Pnative -DskipTests native:compile

native-build-quick:
	./mvnw -pl app -Pnative -DskipTests -DquickBuild=true native:compile

native-test:
	./mvnw -pl app -PnativeTest test

native-smoke:
	RUNTIME=native scripts/native-smoke-test.sh

run-native:
	./app/target/springlite-app

image-jvm:
	docker build -f Dockerfile.jvm -t springlite:jvm .

image-native:
	docker build --build-arg QUICK_BUILD=$${QUICK_BUILD:-false} -f Dockerfile.native -t springlite:native .

benchmark-jvm:
	RUNTIME=jvm scripts/benchmark.sh

benchmark-native:
	RUNTIME=native scripts/benchmark.sh

compare-runtimes:
	scripts/compare-runtimes.sh

measure-startup:
	scripts/measure-startup.sh

measure-memory:
	scripts/measure-memory.sh

postgres-up:
	docker compose up -d --wait postgres

postgres-down:
	docker compose down

clean:
	./mvnw clean
