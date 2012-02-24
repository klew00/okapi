@echo off
if not x==%1x goto Start
cls
echo.
echo This batch file is designed to be run from a shortcut to specify the folder
echo where the Lynx executable is located.
echo Use startLynxPrompt to open a DOS window for Lynx.
echo.
pause
goto End

:Start
set PATH=%1;%PATH%
lynx -info

:End
