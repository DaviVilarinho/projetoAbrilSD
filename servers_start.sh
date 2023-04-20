#!/bin/bash



for i in {1..5}
do
  ./gradlew run -PmainClassName=ufu.davigabriel.server.AdminPortalServer --args="$i" &
done