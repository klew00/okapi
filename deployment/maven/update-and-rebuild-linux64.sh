#!/bin/bash -e

cd ../..
mvn clean
mvn install

cd deployment/maven
ant -f build_getVerProp.xml

ant -f build_okapi-lib.xml

ant -f build_okapi-apps.xml -Dplatform=gtk2-linux-x86_64

ant -f build_okapi-plugins.xml

ant -f build_omegat-plugins.xml

chmod a+x dist_gtk2-linux-x86_64/tikal.sh
chmod a+x dist_gtk2-linux-x86_64/rainbow.sh
chmod a+x dist_gtk2-linux-x86_64/ratel.sh
chmod a+x dist_gtk2-linux-x86_64/checkmate.sh

cd ../../applications/integration-tests
mvn clean integration-test
