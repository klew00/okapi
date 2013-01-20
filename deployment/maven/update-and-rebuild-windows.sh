#!/bin/bash -e

cd ../..
#mvn clean
#mvn install

cd deployment/maven
ant -f build_getVerProp.xml

ant -f build_okapi-lib.xml

ant -f build_okapi-apps.xml -Dplatform=win32-x86

ant -f build_okapi-plugins.xml

ant -f build_omegat-plugins.xml

cd ../../applications/integration-tests
mvn clean integration-test

