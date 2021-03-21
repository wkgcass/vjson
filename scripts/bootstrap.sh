#!/bin/bash

set -e

version="$1"
if [ -z "$version" ]
then
	echo "the first argument should be version string"
	exit 1
fi

./gradlew clean jar
cp build/libs/"vjson-$version.jar" ./bootstrap/vjson-bootstrap.jar
