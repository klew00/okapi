call ant -f build_okapi-plugins.xml
if ERRORLEVEL 1 goto end

:end
pause