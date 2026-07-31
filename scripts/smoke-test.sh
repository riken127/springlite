#!/usr/bin/env sh
set -eu

base_url="${1:-http://localhost:8080}"
curl --fail --silent --show-error "$base_url/actuator/health/liveness" >/dev/null
curl --fail --silent --show-error "$base_url/actuator/health/readiness" >/dev/null
created=$(curl --fail --silent --show-error -H 'Content-Type: application/json' \
  -d "{\"name\":\"smoke-$(date +%s)\"}" "$base_url/v1/widgets")
widget_id=$(printf '%s' "$created" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')
[ -n "$widget_id" ] || { echo "Smoke test did not receive a widget id" >&2; exit 1; }
curl --fail --silent --show-error "$base_url/v1/widgets/$widget_id" | grep '"name"' >/dev/null
printf '%s\n' "Smoke test passed: $widget_id"
