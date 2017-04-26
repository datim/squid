#!/bin/bash

CURRENT_DIR=$(dirname "$0")

echo "----------------------------"
echo "Building Front End"
echo "----------------------------"
webpack

echo "----------------------------"
echo "Building Back End"
echo "----------------------------"
${CURRENT_DIR}/gradlew build
