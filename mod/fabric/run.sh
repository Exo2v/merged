#!/bin/bash
set -e
echo "Hyrulecraft — Fabric host build"
if ! command -v java &> /dev/null; then
  echo "JDK 17 required — install from https://adoptium.net"
  echo "On Debian/Ubuntu: sudo apt install openjdk-17-jdk"
  exit 1
fi
echo "java:" $(java -version 2>&1 | head -n1)
./gradlew build --info
echo "Done → build/libs/hyrulecraft-*.jar"
echo "For dev client: ./gradlew runClient"
