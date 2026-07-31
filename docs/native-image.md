# Native Image

GraalVM Native Image analyzes a closed world at build time. Dynamically discovered reflection,
resources, serialization types, proxies, or class initialization may be invisible unless Spring or
the dependency supplies reachability metadata.

Spring Boot AOT generates the application context and hints through the official Maven integration.
The template currently needs no custom runtime hints. Do not add speculative bulk reflection JSON.
If a concrete dependency proves that a hint is needed, place one documented
`RuntimeHintsRegistrar` in `nativeimage/`, state the affected class/resource and dependency, import
it explicitly, and add a test that traverses the behavior.

```bash
make native-build-quick  # iterative -Ob/quick-build path
make native-build        # normal release-oriented optimization
make native-smoke        # readiness + create + read + graceful SIGTERM
make native-test         # native test support when GraalVM is installed
```

`make image-native` builds the release-oriented container. On a constrained local Docker Desktop,
`QUICK_BUILD=true make image-native` selects the lower-memory quick-build mode. Keep the default
for release artefacts and allocate at least 8 GiB to the native-image builder.

When a build fails:

1. reproduce with the normal JVM using `-Dspring.aot.enabled=true`;
2. inspect Spring AOT generated sources/resources under `app/target/spring-aot`;
3. read the first missing-registration or class-initialization error, not only the final summary;
4. check the GraalVM reachability metadata repository and the dependency's native support;
5. use the tracing agent only on representative paths, review its output, and minimize it;
6. add a focused smoke/integration test before committing a hint.

Native debugging and sampling differ from HotSpot; build debug information when needed and use OS
tools or Native Image monitoring features supported by the selected distribution. Dynamic class
loading, runtime bytecode generation, serialization, JNI, proxies, and resources deserve explicit
review. PGO is an optional advanced release optimization and is not part of the mandatory workflow.
