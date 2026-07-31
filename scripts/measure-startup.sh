#!/usr/bin/env sh
set -eu

runtime="${RUNTIME:-jvm}"
BENCHMARK_DURATION=1s BENCHMARK_CONNECTIONS=1 RUNTIME="$runtime" scripts/benchmark.sh
