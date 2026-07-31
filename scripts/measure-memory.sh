#!/usr/bin/env sh
set -eu

runtime="${RUNTIME:-jvm}"
BENCHMARK_DURATION="${BENCHMARK_DURATION:-5s}" RUNTIME="$runtime" scripts/benchmark.sh
