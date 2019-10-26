#!/bin/bash

# switch to knx-link folder (regardless where the script is executed)
SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE}" )" >/dev/null 2>&1 && pwd )"
cd $SCRIPTS_DIR/..

# delete 'knx.log'
rm -f knx.log

# delete all with:
#   knx-*.log
#   knx-*.log.0000-00-00.gz
find knx-* -type f -regex '.*\.log\(\.[0-9-]+\.gz\)?$' -delete
