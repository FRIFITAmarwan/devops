#!/bin/sh

echo "The app is starting ..."
exec java -jar -Dspring.profiles.active=${SPRING_ACTIVE_PROFILES} "DevOps-0.0.1-SNAPSHOT.jar"