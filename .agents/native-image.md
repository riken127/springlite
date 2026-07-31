# Native task checklist

Reproduce with Spring AOT before adding hints. Any hint must identify the dependency and reason,
live in `nativeimage`, and have a test. Validate with `make native-build && make native-smoke`.
