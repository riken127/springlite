#!/usr/bin/env sh
set -eu

java_major=$(java -version 2>&1 | sed -n '1s/.*version "\([0-9]*\).*/\1/p')
[ "${java_major:-0}" -ge 25 ] || { echo "Java 25 or newer is required for AOT Cache." >&2; exit 1; }

./mvnw -pl app -am -Pnative -DskipTests package
rm -rf app/application
java -Djarmode=tools -jar app/target/springlite-app-0.1.0-SNAPSHOT.jar extract --destination app/application
(
  cd app/application
  java -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh \
    -Dspring.aot.enabled=true -jar springlite-app-0.1.0-SNAPSHOT.jar
)
printf '%s\n' 'AOT cache created at app/application/app.aot'
