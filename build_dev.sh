#!/bin/bash
# Build and launch application in 'debug' mode
#

CURRENT_DIR=$(dirname "$0")
ROOT_DIR="C:/users/datim/Desktop/squid"
LOG_LEVEL=debug
SPACER="---------------------------"

# compile front end with webpack
printf "%s\n Building Front End\n%s\n" $SPACER $SPACER
webpack --config webpack.config.dev.js

# compile back end with Java
printf "%s\n Building Back End\n%s\n" $SPACER $SPACER
${CURRENT_DIR}/gradlew clean build

# launch application with 'debug' configuration
printf "%s\n Launching\n%s\n" $SPACER $SPACER
java -DROOT=${ROOT_DIR} -DLOG_LEVEL=${LOG_LEVEL} -jar build/libs/squid-1.0.jar  --spring.config.name=debug.properties
