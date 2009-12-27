#!/bin/bash -e

cd ../..
svn update
mvn clean
mvn install

cd deployment/maven
ant -f build_okapi-lib.xml

ant -f build_okapi-apps.xml -Dplatform=gtk2-linux-x86

chmod a+x dist_gtk2-linux-x86/tikal.sh
chmod a+x dist_gtk2-linux-x86/rainbow.sh
chmod a+x dist_gtk2-linux-x86/ratel.sh

cd ../../applications/integration-tests
mvn integration-test
