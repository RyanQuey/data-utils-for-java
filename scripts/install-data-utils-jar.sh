#!/bin/bash -eux

if [ "$BASH" != "/bin/bash" ]; then
  echo "Please do ./$0"
  exit 1
fi

# puts this jar into local mvn repo, so can be imported into project through the pom

# always base everything relative to this file to make it simple
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

# This assumes parent project has a scripts dir with layout like this. If it doesn't, comment out
. $parent_path/../../scripts/config/env-vars.sh

#version=${DATA_UTILS_VERSION:-0.5.0}

#artifactId=data-utils

# This way works, but we don't need this level of specificity right now. Just do mvn install, and that will update the version based on the pom which is better
# mvn install:install-file \
#   -Dfile=$parent_path/../target/$artifactId-$version.jar \
#   -DgroupId=com.ryanquey \
#   -DartifactId=$artifactId \
#   -Dversion=$version \
#   -Dpackaging=jar \
#   -DgeneratePom=true

cd $parent_path/..
mvn clean install
