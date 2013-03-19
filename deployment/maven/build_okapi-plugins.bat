call ant -f build_getVerProp.xml
if ERRORLEVEL 1 goto end

cd ../../okapi/libraries/lib-languagetool
call mvn clean package

cd ../../../deployment/maven
call ant -f build_okapi-plugins.xml
if ERRORLEVEL 1 goto end

:end
pause