#!/bin/bash
# Build and launch application in 'production' mode
#

CURRENT_DIR=$(dirname "$0")
ROOT_DIR="C:/users/datim/Desktop/squid"
LOG_LEVEL=info
SPACER="---------------------------"

# compile front end with webpack
printf "%s\n Building Front End\n%s\n" $SPACER $SPACER
webpack --config webpack.config.js

# compile back end with Java
printf "%s\n Building Back End\n%s\n" $SPACER $SPACER
${CURRENT_DIR}/gradlew clean build

# launch application with 'production' configuration
printf "%s\n Launching\n%s\n" $SPACER $SPACER
java -DROOT=${ROOT_DIR} -DLOG_LEVEL=${LOG_LEVEL} -jar build/libs/squid-1.0.jar
