#!/usr/bin/env sh
set -eu

usage() {
  cat <<'USAGE'
Usage: scripts/init-template.sh --service-name NAME --package PACKAGE [options]

Required:
  --service-name NAME       Lowercase runtime name, for example orders-service.
  --package PACKAGE         Java package, for example com.acme.orders.

Options:
  --group-id GROUP          Defaults to the first two package parts.
  --root-artifact-id ID     Defaults to SERVICE.
  --app-artifact-id ID      Defaults to SERVICE-app.
  --database-name NAME      Defaults to SERVICE with dashes changed to underscores.
  --dry-run                 Show the plan without modifying files.
  -h, --help                Show this help.
USAGE
}

service_name="" package_name="" group_id="" root_artifact_id="" app_artifact_id=""
database_name="" dry_run=false

need_value() { [ "$#" -ge 2 ] && [ -n "$2" ] || { echo "$1 requires a value" >&2; exit 2; }; }

while [ "$#" -gt 0 ]; do
  case "$1" in
    --service-name) need_value "$@"; service_name=$2; shift 2 ;;
    --package) need_value "$@"; package_name=$2; shift 2 ;;
    --group-id) need_value "$@"; group_id=$2; shift 2 ;;
    --root-artifact-id) need_value "$@"; root_artifact_id=$2; shift 2 ;;
    --app-artifact-id) need_value "$@"; app_artifact_id=$2; shift 2 ;;
    --database-name) need_value "$@"; database_name=$2; shift 2 ;;
    --dry-run) dry_run=true; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; usage >&2; exit 2 ;;
  esac
done

[ -n "$service_name" ] && [ -n "$package_name" ] || {
  echo "--service-name and --package are required." >&2; usage >&2; exit 2;
}
case "$service_name" in *[!a-z0-9-]*|""|-*|*-) echo "Invalid --service-name." >&2; exit 2;; esac
case "$package_name" in *[!a-zA-Z0-9_.]*|.*|*.|*..*|*[A-Z]*) echo "Invalid --package." >&2; exit 2;; esac
case "$package_name" in *.*) :;; *) echo "--package needs at least two parts." >&2; exit 2;; esac

[ -n "$group_id" ] || group_id=$(printf '%s' "$package_name" | awk -F. '{print $1 "." $2}')
[ -n "$root_artifact_id" ] || root_artifact_id=$service_name
[ -n "$app_artifact_id" ] || app_artifact_id="${service_name}-app"
[ -n "$database_name" ] || database_name=$(printf '%s' "$service_name" | tr - _)

for value in "$group_id" "$root_artifact_id" "$app_artifact_id" "$database_name"; do
  case "$value" in *[!a-zA-Z0-9_.-]*|""|.*|*.) echo "Invalid identifier: $value" >&2; exit 2;; esac
done

old_package=com.example.service
old_path=com/example/service
new_path=$(printf '%s' "$package_name" | tr . /)

cat <<PLAN
Template initialization plan:
  service name:       springlite -> $service_name
  package:            $old_package -> $package_name
  groupId:            com.example -> $group_id
  root artifactId:    springlite -> $root_artifact_id
  app artifactId:     springlite-app -> $app_artifact_id
  database:           springlite -> $database_name
PLAN

[ "$dry_run" = false ] || exit 0
[ -d app/src/main/java/$old_path ] || {
  echo "Template already initialized or package root missing." >&2; exit 1;
}

replace() {
  file=$1
  [ -f "$file" ] || return 0
  sed -i.bak \
    -e "s|$old_package|$package_name|g" \
    -e "s|$old_path|$new_path|g" \
    -e "s|springlite-app|$app_artifact_id|g" \
    -e "s|com.example|$group_id|g" \
    -e "s|Springlite|$service_name|g" \
    -e "s|springlite|$service_name|g" "$file"
  rm -f "$file.bak"
}

replace_database_lines() {
  file=$1
  [ -f "$file" ] || return 0
  sed -i.bak \
    -e "/DATABASE/s|springlite|$database_name|g" \
    -e "/POSTGRES/s|springlite|$database_name|g" "$file"
  rm -f "$file.bak"
}

for file in .env.example docker-compose.yml app/src/main/resources/application.yml; do
  replace_database_lines "$file"
done

git ls-files -co --exclude-standard | while IFS= read -r file; do
  case "$file" in
    scripts/init-template.sh|*.class|*/target/*|*.jar|*.aot) ;;
    *.java|*.xml|*.md|*.yml|*.yaml|*.properties|*.example|*.sh|Dockerfile*|Makefile|.gitignore|.dockerignore|.editorconfig) replace "$file" ;;
  esac
done

# Maven coordinates may intentionally differ from the runtime service name.
for file in pom.xml app/pom.xml; do
  sed -i.bak "s|<artifactId>$service_name</artifactId>|<artifactId>$root_artifact_id</artifactId>|g" "$file"
  rm -f "$file.bak"
done

move_root() {
  root=$1 old_dir=$1/$old_path new_dir=$1/$new_path
  [ -d "$old_dir" ] || return 0
  mkdir -p "$(dirname "$new_dir")"
  mv "$old_dir" "$new_dir"
  cleanup=$(dirname "$old_dir")
  while [ "$cleanup" != "$root" ]; do rmdir "$cleanup" 2>/dev/null || break; cleanup=$(dirname "$cleanup"); done
}
move_root app/src/main/java
move_root app/src/test/java

cat <<'NEXT'

Initialization complete. Next commands:
  make format
  make verify
  make dev
  make native-build
NEXT
