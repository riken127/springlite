#!/usr/bin/env sh
set -eu

for tool in docker oha curl; do
  command -v "$tool" >/dev/null 2>&1 || { echo "$tool is required." >&2; exit 1; }
done

docker compose up -d --wait postgres
cleanup() { docker compose down >/dev/null 2>&1 || true; }
trap cleanup EXIT INT TERM

./mvnw -q -pl app -am -DskipTests package
scripts/package-aot-cache.sh
[ -x app/target/springlite-app ] || ./mvnw -q -pl app -Pnative -DskipTests native:compile

run_and_find() {
  mode=$1
  RUNTIME=$mode scripts/benchmark.sh >&2
  find benchmarks/results -type f -path "*/${mode}-*/summary.txt" -print | sort | tail -n 1
}

jvm=$(run_and_find jvm)
aot=$(run_and_find aot)
native=$(run_and_find native)
report="benchmarks/results/comparison-$(date -u +%Y%m%dT%H%M%SZ).md"

value() { sed -n "s/^$2=//p" "$1"; }
cat >"$report" <<REPORT
# Runtime comparison

| Metric | JVM | JVM + AOT Cache | Native |
|---|---:|---:|---:|
| Time to readiness (ms) | $(value "$jvm" startup_ms) | $(value "$aot" startup_ms) | $(value "$native" startup_ms) |
| Idle RSS (KiB) | $(value "$jvm" idle_rss_kb) | $(value "$aot" idle_rss_kb) | $(value "$native" idle_rss_kb) |
| RSS after load (KiB) | $(value "$jvm" loaded_rss_kb) | $(value "$aot" loaded_rss_kb) | $(value "$native" loaded_rss_kb) |

Latency percentiles, throughput, error rate and CPU-time samples are in each linked oha JSON result. Results depend on hardware, OS, database state, CPU temperature, JVM flags, warm-up and competing processes.

- JVM: \`$jvm\`
- JVM + AOT Cache: \`$aot\`
- Native: \`$native\`
REPORT

printf '%s\n' "Comparison written to $report"
