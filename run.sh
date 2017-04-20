#!/bin/bash
# Execute the program
LOG_LEVEL=info
ROOT_DIR="C:/users/datim/Desktop/squid"

java -DROOT=${ROOT_DIR} -DLOG_LEVEL=${LOG_LEVEL} -jar build/libs/squid-1.0.jar
