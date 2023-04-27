#!/bin/bash

./gradlew --warning-mode none --console=plain extractIncludeProto extractProto generateProto compileJava processResources classes run -PmainClass=ufu.davigabriel.client.OrderPortalClient
