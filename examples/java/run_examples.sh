#!/bin/bash

java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.html -s pseudo myFile.out-pseudo.html
java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.html -s upper myFile.out-upper.html
java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.html -s pseudo -s upper myFile.out-both.html
java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.odt -s pseudo
java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.properties -s pseudo
java -cp .:../../lib/okapi-lib.jar:example01.jar example01.Main myFile.xml -s pseudo

java -cp .:../../lib/okapi-lib.jar:example02.jar example02.Main myFile.odt

java -cp .:../../lib/okapi-lib.jar:example03.jar example03.Main

java -cp .:../../lib/okapi-lib.jar:example04.jar example04.Main

