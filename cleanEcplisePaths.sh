#!/bin/bash

for i in `find . | grep ".pom.xml$"`; do 
  d=${i%"/pom.xml"};
  echo "Processing $d"

  echo ".. .settings"
  svn rm "$d/.settings"; 
  svn propset svn:ignore .settings $d;

  echo ".. .classpath"
  svn rm "$d/.classpath";
  svn propset svn:ignore .classpath $d;

  echo ".. .project"
  svn rm "$d/.project";
  svn propset svn:ignore .project $d;

  echo ".. target"
  svn rm "$d/target";
  svn propset svn:ignore target $d;

done;
