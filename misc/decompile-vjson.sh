#!/bin/bash

bold=$(tput bold)
normal=$(tput sgr0)

set -e

cd build/libs/
jar_name=`ls | grep '\.jar$'`
unzip -qq -o "$jar_name"

files=`find . -name '*.class'`
for f in $files
do
	echo "${bold}decompiling file: ${f}${normal}" >&2
	javap -v -p "$f"
done
