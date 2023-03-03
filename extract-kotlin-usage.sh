#!/bin/bash

set -e

./gradlew clean jar
./misc/decompile-vjson.sh | ./misc/parse-kotlin-usage.py | tee /tmp/kotlin-usage.log
./misc/decompile-vjson-idea-out.sh | ./misc/parse-kotlin-usage.py | tee -a /tmp/kotlin-usage.log
cat /tmp/kotlin-usage.log | sort -h | uniq > ./kotlin-usage.log
