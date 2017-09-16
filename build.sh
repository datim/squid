#!/bin/bash
# Build application in 'production' or 'debug' mode
#

CURRENT_DIR=$(dirname "$0")
SPACER="---------------------------"
WEB_PACK_CONFIG="webpack.config.js"

if [ "$1" == "debug" ];then
  echo "Compiling in debug mode"
  WEB_PACK_CONFIG="webpack.config.dev.js"

else
  echo "Specify 'debug' to compile in debug mode"

fi

# compile front end with webpack
printf "%s\n Building Front End\n%s\n" $SPACER $SPACER
webpack --config ${WEB_PACK_CONFIG}

# compile back end with Java
printf "%s\n Building Back End\n%s\n" $SPACER $SPACER
${CURRENT_DIR}/gradlew clean build
