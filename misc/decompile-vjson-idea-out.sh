#!/bin/bash

set -e

bold=$(tput bold)
normal=$(tput sgr0)

if [ -d "./out/production/classes" ]; then
	cd ./out/production/classes
	files=`find . -name '*.class'`
	for f in $files
	do
		echo "${bold}decompiling file: ${f}${normal}" >&2
		javap -v -p "$f"
	done
fi
