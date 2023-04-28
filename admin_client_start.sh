#!/bin/bash

if [[ -n $1 ]]
then
  ARGS="--args=\"$1\""
fi

./gradlew --warning-mode none --quiet --console=plain extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.client.AdminPortalClient $ARGS
