#!/bin/bash

if [[ -n $1 ]]
then
  ARGS="--args=\"$1\""
fi

./gradlew extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.server.AdminPortalServer $ARGS
