#!/bin/bash
cd "$(dirname "$0")"
java -d64 -XstartOnFirstThread -jar ../../../lib/rainbow.jar
