#!/bin/bash
java -d32 -XstartOnFirstThread -jar lib/rainbow.jar
# You can also use the following options:
# org.eclipse.swt.internal.carbon.smallFonts
# org.eclipse.swt.internal.carbon.noFocusRing
# to possibly get better display.
# java -XstartOnFirstThread -Dorg.eclipse.swt.internal.carbon.smallFonts -Dorg.eclipse.swt.internal.carbon.noFocusRing -jar lib/rainbow.jar
