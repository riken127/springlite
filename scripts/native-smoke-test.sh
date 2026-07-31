#!/usr/bin/env sh
set -eu

port="${HTTP_PORT:-8080}"
log_file="${SMOKE_LOG:-app/target/native-smoke.log}"
binary="${NATIVE_BINARY:-app/target/springlite-app}"

[ -x "$binary" ] || { echo "Native executable not found; run make native-build." >&2; exit 1; }

"$binary" >"$log_file" 2>&1 &
pid=$!
cleanup() {
  kill -TERM "$pid" 2>/dev/null || true
  wait "$pid" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

attempt=0
until curl --fail --silent "http://localhost:$port/actuator/health/readiness" >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  if [ "$attempt" -ge 60 ]; then
    echo "Native application did not become ready; see $log_file" >&2
    exit 1
  fi
  sleep 1
done

scripts/smoke-test.sh "http://localhost:$port"
