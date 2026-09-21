#!/usr/bin/env bash
set -euo pipefail
# Java 25 is required by the genuine Velocity 4.2 runtime.
: "${VELOCITY_JAR:?Set VELOCITY_JAR to the verified Velocity 4.2.0 JAR}"
: "${VELOCITAB_JAR:?Set VELOCITAB_JAR to the built shaded Velocitab JAR}"
java -Xmx512m -cp "$VELOCITY_JAR:$VELOCITAB_JAR" "$(dirname "$0")/Velocitab263PacketHarness.java"
