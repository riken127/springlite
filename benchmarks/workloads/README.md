# Workloads

`cpu` serializes a small dynamic response without database access. `postgresql` executes a real
jOOQ query and maps rows into domain and HTTP objects. Both are warmed before measurement.
