# Runtime modes

| Mode | Strengths | Costs | Good fit |
|---|---|---|---|
| JVM | adaptive JIT, mature profiling, strongest peak throughput potential | warm-up and larger runtime footprint | long-running or CPU-heavy services |
| JVM + AOT Cache | faster startup/lower footprint while retaining HotSpot | cache is tied to the exact app/JDK; requires training | JVM deployments restarted frequently |
| Native Image | fast readiness, compact closed-world runtime, no JIT warm-up | slow build, different debugging/profiling, closed-world constraints | scale-to-zero, aggressive autoscaling, tight memory |

`make package-aot-cache` packages Spring AOT code, extracts the executable jar, and performs the
official Java 25 training run with `-XX:AOTCacheOutput`. `make run-aot-cache` uses the extracted jar
with `-XX:AOTCache`. The exact Java version and application bits must match. AOT Cache supersedes
CDS for Java 25: CDS primarily archives classes, while AOT Cache can include ahead-of-time method
profiles/code. Neither is a closed-world standalone executable like Native Image.

`make native-build` performs Spring AOT analysis and GraalVM compilation. `make
native-build-quick` uses Native Build Tools' quick-build mode for iteration; release validation uses
the normal optimization level. Do not infer throughput superiority from startup results.

Containers follow the same distinction. The JVM image runs the jar; an AOT Cache image can be
derived from `Dockerfile.jvm` by training/extracting with the same JDK image; the native image ships
only the executable, CA certificates, timezone data, and required OS libraries.
