#!/bin/bash

mvn install:install-file -DgroupId=org.eclipse.swt -DartifactId=swt-win-32 -Dversion=3.5 -Dfile=third-party/win32-x86/org.eclipse.swt.win32.win32.x86_3.5.0.v3550b.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.eclipse.swt -DartifactId=swt-cocoa-macosx -Dversion=3.5 -Dfile=third-party/cocoa-macosx/org.eclipse.swt.cocoa.macosx_3.5.0.v3550b.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.eclipse.swt -DartifactId=swt-gtk2-linux-x86 -Dversion=3.5 -Dfile=third-party/gtk2-linux-x86/swt.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.eclipse.swt -DartifactId=swt-gtk2-linux-x86_64 -Dversion=3.5 -Dfile=third-party/gtk2-linux-x86_64/swt.jar -Dpackaging=jar -DgeneratePom=true


