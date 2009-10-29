This module is to help with the automated Tycho build process.

Currently, the idea is to do the following steps:

- Build all of okapi (mvn clean install).
   You must package (mvn package or mvn install which in turn calls package) the libraries.
- Build this module which does the following:
    - Backs up the eclipse.build.location\plugins directory, creating a plugins.zip file.
    - Copies all libraries installed under the "net.sf.okapi" groupId to the eclipse.build.location/plugins
      directory.
    - Copies all 3rd party OSGi bundles installed in the .m2/repository/OkapiEclipseTargetPlatform directory
      to the eclipse.build.location/plugins directory
    - It is highly recommended that a separate Eclipse install be used. To use for the tycho build, the eclipse
      delta pack must all be installed in the same location.
- Separately, run the tycho build against only the Eclipse RCP application to be built, specifying
   -Dtycho.targetPlatform=eclipse.build.location 
     - This will currently need to be done for each supported platform (OS X, Linux, Windows).