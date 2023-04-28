#!/bin/bash

./gradlew extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.server.OrderPortalServer --args="$1"
