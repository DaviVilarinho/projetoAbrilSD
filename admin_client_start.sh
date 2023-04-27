#!/bin/bash

./gradlew --warning-mode none --quiet --console=plain extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.client.AdminPortalClient
