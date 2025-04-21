#!/bin/bash
export MAVEN_OPTS="--add-modules jdk.compiler --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"
mvn clean install "$@"
if [ $? -eq 0 ]; then
    echo "Build successful! Running application..."
    mvn spring-boot:run
else
    echo "Build failed with error code $?"
fi 