#!/bin/bash
# launch application in 'production' or 'debug' mode
#

CURRENT_DIR=$(dirname "$0")
ROOT_DIR="C:/users/datim/Desktop/squid"
LOG_LEVEL=info
EXTRA_PROPS=""

if [ "$1" == "debug" ];then
  echo "Executing in debug mode"
  LOG_LEVEL=debug
  EXTRA_PROPS="--spring.config.name=debug.properties"

else
  echo "Specify 'debug' to execute in debug mode"

fi

# launch application with 'production' configuration
printf "%s\n Launching\n%s\n" $SPACER $SPACER
echo "Root directory is ${ROOT_DIR}"
echo "java -DROOT=${ROOT_DIR} -DLOG_LEVEL=${LOG_LEVEL} -jar build/libs/squid-1.0.jar ${EXTRA_PROPS}"
java -DROOT=${ROOT_DIR} -DLOG_LEVEL=${LOG_LEVEL} -jar build/libs/squid-1.0.jar ${EXTRA_PROPS}
