#!/bin/sh
# From gist at https://gist.github.com/chadmaughan/5889802

if (./gradlew ktLintCheck && ./gradlew detekt); then
  echo "Pre Commit Checks Passed -- no problems found" >&2
  exit 0
else
  echo "Pre Commit Checks Failed. Please fix the above issues before committing" >&2
  exit 1
fi
