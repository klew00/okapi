#!/bin/bash

for i in `find . | grep ".pom.xml$"`; do 
  d=${i%"/pom.xml"};
  echo "Processing $d"
  svn rm "$d/.settings"; 
  svn rm "$d/.classpath";
  svn rm "$d/.project";
  svn rm "$d/.pydevproject";
  svn rm "$d/target";

  svn propset svn:ignore '
.project
.classpath
target
.settings
' $d;

done;
