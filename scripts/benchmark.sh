#!/usr/bin/env sh
set -eu

runtime="${RUNTIME:-jvm}"
duration="${BENCHMARK_DURATION:-15s}"
connections="${BENCHMARK_CONNECTIONS:-20}"
port="${HTTP_PORT:-8080}"
base_url="http://localhost:$port"
timestamp=$(date -u +%Y%m%dT%H%M%SZ)
result_dir="benchmarks/results/${runtime}-${timestamp}"
log_file="$result_dir/application.log"

command -v oha >/dev/null 2>&1 || { echo "oha is required (https://github.com/hatoo/oha)." >&2; exit 1; }
command -v curl >/dev/null 2>&1 || { echo "curl is required." >&2; exit 1; }
mkdir -p "$result_dir"

case "$runtime" in
  jvm)
    ./mvnw -q -pl app -am -DskipTests package
    java -jar app/target/springlite-app-0.1.0-SNAPSHOT.jar >"$log_file" 2>&1 & ;;
  aot)
    [ -f app/application/app.aot ] || scripts/package-aot-cache.sh
    (cd app/application && exec java -XX:AOTCache=app.aot -jar springlite-app-0.1.0-SNAPSHOT.jar) >"$log_file" 2>&1 & ;;
  native)
    [ -x app/target/springlite-app ] || { echo "Run make native-build first." >&2; exit 1; }
    app/target/springlite-app >"$log_file" 2>&1 & ;;
  *) echo "RUNTIME must be jvm, aot, or native." >&2; exit 2 ;;
esac
pid=$!
cleanup() { kill -TERM "$pid" 2>/dev/null || true; wait "$pid" 2>/dev/null || true; }
trap cleanup EXIT INT TERM

attempt=0
until curl --fail --silent "$base_url/actuator/health/readiness" >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  [ "$attempt" -lt 600 ] || { echo "Application did not become ready; see $log_file" >&2; exit 1; }
  sleep 0.1
done

startup_ms=$((attempt * 100))
idle_rss_kb=$(ps -o rss= -p "$pid" | tr -d ' ')

# Warm both paths before recording latency distributions.
oha -n 100 -c 5 "$base_url/v1/system/ping" >/dev/null
curl --fail --silent -H 'Content-Type: application/json' \
  -d "{\"name\":\"benchmark-$timestamp\"}" "$base_url/v1/widgets" >/dev/null

oha -z "$duration" -c "$connections" --json "$base_url/v1/system/ping" >"$result_dir/cpu.json"
oha -z "$duration" -c "$connections" --json "$base_url/v1/widgets?limit=20" >"$result_dir/postgresql.json"
loaded_rss_kb=$(ps -o rss= -p "$pid" | tr -d ' ')

cat >"$result_dir/summary.txt" <<SUMMARY
runtime=$runtime
startup_ms=$startup_ms
idle_rss_kb=$idle_rss_kb
loaded_rss_kb=$loaded_rss_kb
duration=$duration
connections=$connections
cpu_result=$result_dir/cpu.json
postgresql_result=$result_dir/postgresql.json
SUMMARY

printf '%s\n' "Benchmark complete: $result_dir/summary.txt"
