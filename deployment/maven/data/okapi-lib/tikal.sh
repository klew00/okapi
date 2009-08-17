#!/bin/bash

## Linux
LSOF=$(lsof -p $$ | grep -E "/"$(basename $0)"$")
MY_PATH=$(echo $LSOF | sed -r s/'^([^\/]+)\/'/'\/'/1 2>/dev/null)
if [ $? -ne 0 ]; then
## OSX
MY_PATH=$(echo $LSOF | sed -E s/'^([^\/]+)\/'/'\/'/1 2>/dev/null)
fi

java -jar MY_ROOT=$(dirname $MY_PATH)/lib/tikal.jar $1 $2 $3 $4 $5 $6 $7 $8 $9
