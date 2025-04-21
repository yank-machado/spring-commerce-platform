@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED
mvn clean install %*
if %ERRORLEVEL% EQU 0 (
    echo Build successful! Running application...
    mvn spring-boot:run
) else (
    echo Build failed with error code %ERRORLEVEL%.
) 