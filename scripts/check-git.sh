#!/bin/bash

set -e

output=`git status --short`

set +e

output=`echo "$output" | grep -v '\bvjson\-bootstrap\.jar$'`

set -e

if [ ! -z "$output" ]
then
	echo "This operation will modify source codes"
	echo "Please make git directory clean before performing this operation"
	echo "Current status:"
	echo "$output"
	exit 1
fi
echo "Git is clean"
exit 0
