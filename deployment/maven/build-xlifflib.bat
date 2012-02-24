cd ../../okapi/libraries/lib-xliff/
call mvn install
if ERRORLEVEL 1 goto end

cd ../../applications/lynx
call mvn install
if ERRORLEVEL 1 goto end

cd ../../../deployment/maven
call ant -f build_xliff-lib.xml
if ERRORLEVEL 1 goto end

:end
pause